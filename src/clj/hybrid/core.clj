(ns hybrid.core
  (:require [compojure.route :refer (files not-found)]
            [compojure.handler :refer (site)]
            [compojure.core :refer (defroutes GET POST DELETE ANY context)]
            [compojure.route :as route]
            [org.httpkit.server :refer :all]
            [taoensso.sente :as sente]
            [clojure.core.async :refer (<!! go-loop)]
            [com.stuartsierra.component :as component]))

(let [{:keys [ch-recv send-fn ajax-post-fn ajax-get-or-ws-handshake-fn
              connected-uids]}
      (sente/make-channel-socket! {})]
  (defonce ring-ajax-post                ajax-post-fn)
  (defonce ring-ajax-get-or-ws-handshake ajax-get-or-ws-handshake-fn)
  (defonce ch-chsk                       ch-recv) ; ChannelSocket's receive channel
  (defonce chsk-send!                    send-fn) ; ChannelSocket's send API fn
  (defonce connected-uids                connected-uids) ; Watchable, read-only atom
  )

(defn- event-msg-handler
  [{:as ev-msg :keys [ring-req event ?reply-fn]} _]
  (let [session (:session ring-req)
        uid     (:uid session)
        [id data :as ev] event]

    (println "Event: %s" ev)))

(defonce chsk-router
    (sente/start-chsk-router-loop! event-msg-handler ch-chsk))

(defroutes my-app
  ;; <other stuff>

    ;;; Add these 2 entries: --->
  (GET  "/chsk" req (ring-ajax-get-or-ws-handshake req))
  (POST "/chsk" req (ring-ajax-post                req))
  (route/resources "/"))

(defrecord WebServer []
  component/Lifecycle

  (start [component]
    (if-not (= (:status component) :running)
      (do
        (println ";; Starting webserver")
        (let [server (run-server (site #'my-app) {:port 3000})]
          (assoc component :server server :status :running)))
      (do
        (println ";; Server already started")
        component)))
  (stop [component]
    (println ";; Stoping webserver")
    ((:server component))
    (dissoc (assoc component :status :stopped)
            :server)))

(defn web-server []
  (map->WebServer {}))

(defonce system (web-server))

(alter-var-root #'system component/start)

(alter-var-root #'system component/stop)
