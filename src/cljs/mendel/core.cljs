(ns mendel.core
  (:require-macros
   [cljs.core.async.macros :as asyncm :refer (go go-loop)])
  (:require
   [figwheel.client :as fw]
   [cljs.core.async :as async :refer (<!! <! >! put! chan)]
   [taoensso.sente :as sente :refer (cb-success?)]
   [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)

;; This gets jsPlumb ready
(.ready js/jsPlumb #())

;; Binding to a dom element
(.setContainer js/jsPlumb (.getElementById js/document "main-area"))


(defonce sente-socket
  (sente/make-channel-socket! "/chsk" ; Note the same path as before
                              {:type :auto ; e/o #{:auto :ajax :ws}
                               }))

(let [{:keys [chsk ch-recv send-fn state]}
      sente-socket]
  (defonce chsk       chsk)
  (defonce ch-chsk    ch-recv) ; ChannelSocket's receive channel
  (defonce chsk-send! send-fn) ; ChannelSocket's send API fn
  (defonce chsk-state state)   ; Watchable, read-only atom
  )

;; define your app data so that it doesn't get over-written on reload
;; (defonce app-data (atom {}))

(defonce universe (atom {:counter 0
                         :nodes []}))
(defn create-node
  []
  (let [state      @universe
        node-count (:counter state)]
    (swap! universe #(assoc state
                       :counter (inc (:counter state))
                       :nodes (conj (:nodes state)
                                    {:id (str "hello-" node-count)}))))
  )

(defn plumb-it-up []
  (.draggable js/jsPlumb "hello-0"))

(defn state-ful-with-atom []
  [:div {:id "main-area"}
   [:div {:id "add-node" :on-click #(create-node) :style {:position "absolute" :height "50px" :width "200px" :top "50px" :left "50px" :background-color "gray"}}
    "Add Node"]
   (for [node (:nodes @universe)]
     [:div {:id (:id node) :on-mouse-up #(chsk-send! [:hello.blah/world {:hello "world"}]) :style {:position "absolute" :height "50px" :width "50px" :top "200px" :left "200px" :background-color "#66CCFF"}}])])

(defn mount-it []
  (reagent/render-component [state-ful-with-atom]
                            (.-body js/document)))

(defn unmount-it []
  (reagent/unmount-component-at-node (.-body js/document)))

(mount-it)

;; Make a div draggable with the id draggable
;; (.draggable js/jsPlumb "draggable2")

(fw/watch-and-reload
 :websocket-url "ws://localhost:3449/figwheel-ws"
 :jsload-callback (fn []
                    (reset! click-count 0)
                    (unmount-it)
                    (mount-it)))
