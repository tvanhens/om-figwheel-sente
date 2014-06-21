(ns mendel.core
  (:require-macros
   [cljs.core.async.macros :as asyncm :refer (go go-loop)])
  (:require
   [figwheel.client :as fw]
   [cljs.core.async :as async :refer (<!! <! >! put! chan)]
   [taoensso.sente :as sente :refer (cb-success?)]
   [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)

(let [{:keys [chsk ch-recv send-fn state]}
      (sente/make-channel-socket! "/chsk" ; Note the same path as before
                                  {:type :auto ; e/o #{:auto :ajax :ws}
                                   })]
  (def chsk       chsk)
  (def ch-chsk    ch-recv) ; ChannelSocket's receive channel
  (def chsk-send! send-fn) ; ChannelSocket's send API fn
  (def chsk-state state)   ; Watchable, read-only atom
  )

;; define your app data so that it doesn't get over-written on reload
;; (defonce app-data (atom {}))

(println "yo dog blue")

(fw/watch-and-reload
 :websocket-url "ws://localhost:3449/figwheel-ws"
 :jsload-callback (fn []
                    ;; (stop-and-start-my app)
                    ))
