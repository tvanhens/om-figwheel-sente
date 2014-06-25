(ns mendel.core
  (:require-macros
   [cljs.core.async.macros :as asyncm :refer (go go-loop)])
  (:require
   [figwheel.client :as fw]
   [cljs.core.async :as async :refer (<!! <! >! put! chan)]
   [taoensso.sente :as sente :refer (cb-success?)]
   [om.core :as om :include-macros true]
   [om.dom :as dom :include-macros true]))

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

(defonce universe (atom 0))

(defn canvas [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/div #js {:style #js {:position "relative"}}
               (dom/div #js {:id "hello-0"
                             :style #js {:position "absolute"
                                         :backgroundColor "blue"
                                         :top    100
                                         :left   200
                                         :height 50
                                         :width  100}} "Hello")
               (dom/div #js {:id "hello-1"
                             :style #js {:position "absolute"
                                         :backgroundColor "red"
                                         :top    100
                                         :left   200
                                         :height 50
                                         :width  100}} "Hello")
               (dom/h1 nil data)
               (dom/button #js {:onClick #(swap! universe inc)} "Inc")))))

(om/root canvas universe
         {:target (.-body js/document)})


#_(defn create-node
    []
    (let [state      @universe
          node-count (:counter state)]
      (om/transact! universe #(assoc state
                                :counter (inc (:counter state))
                                :nodes (conj (:nodes state)
                                             {:id (str "hello-" node-count)})))
      (println @universe)))

#_(defn plumb-it-up []
    (.draggable js/jsPlumb "hello-0"))

#_(defn state-ful-with-atom []
  [:div {:id "main-area"}
   [:div {:id "add-node" :on-click #(create-node) :style {:position "absolute" :height "50px" :width "200px" :top "50px" :left "50px" :background-color "gray"}}
    "Add Node"]
   (for [node (:nodes @universe)]
     [:div {:id (:id node) :on-mouse-up #(chsk-send! [:hello.blah/world {:hello "world"}]) :style {:position "absolute" :height "50px" :width "50px" :top "200px" :left "200px" :background-color "#66CCFF"}}])])

#_(defn mount-it []
    (reagent/render-component [state-ful-with-atom]
                              (.-body js/document)))

#_(defn unmount-it []
    (reagent/unmount-component-at-node (.-body js/document)))

#_(mount-it)

;; Make a div draggable with the id draggable
(.draggable js/jsPlumb "hello-0")
(.draggable js/jsPlumb "hello-1")


(fw/watch-and-reload
 :websocket-url "ws://localhost:3449/figwheel-ws"
 :jsload-callback (fn []
                    #_(unmount-it)
                    #_(mount-it)))
