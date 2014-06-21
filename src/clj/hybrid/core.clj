(ns hybrid.core
  (:require [compojure.route :refer (files not-found)]
            [compojure.handler :refer (site)]
            [compojure.core :refer (defroutes GET POST DELETE ANY context)]
            [compojure.route :as route]
            [org.httpkit.server :refer :all]
            [taoensso.sente :as sente]
            [clojure.core.async :refer (<!! go-loop)]))

(let [{:keys [ch-recv send-fn ajax-post-fn ajax-get-or-ws-handshake-fn
              connected-uids]}
      (sente/make-channel-socket! {})]
  (def ring-ajax-post                ajax-post-fn)
  (def ring-ajax-get-or-ws-handshake ajax-get-or-ws-handshake-fn)
  (def ch-chsk                       ch-recv) ; ChannelSocket's receive channel
  (def chsk-send!                    send-fn) ; ChannelSocket's send API fn
  (def connected-uids                connected-uids) ; Watchable, read-only atom
  )

(defroutes my-app
  ;; <other stuff>

    ;;; Add these 2 entries: --->
  (GET  "/chsk" req (ring-ajax-get-or-ws-handshake req))
  (POST "/chsk" req (ring-ajax-post                req))
  (route/resources "/"))

(run-server (site #'my-app) {:port 3000})
