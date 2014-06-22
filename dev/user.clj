(ns user
  (:require [hybrid.core]
            [com.stuartsierra.component :as component]
            [clojure.tools.namespace.repl :refer (refresh)]))

(def system nil)

(defn init []
  (alter-var-root #'system
                  (constantly (hybrid.core/web-server))))

(defn start []
  (alter-var-root #'system component/start))

(defn stop []
  (alter-var-root #'system
                  (fn [s] (when s (component/stop s)))))

(defn go []
  (init)
  (start))

(defn reset []
  (stop)
  (refresh :after 'user/go))
