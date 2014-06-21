(defproject mendel "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2202"]
                 [figwheel "0.1.3-SNAPSHOT"]
                 [org.clojure/core.async "0.1.278.0-76b25b-alpha"]
                 [sablono "0.2.16"]
                 [om "0.6.2"]
                 [com.taoensso/sente "0.14.1"]
                 [compojure "1.1.8"]
                 [ring "1.3.0"]
                 [http-kit "2.1.16"]
                 [reagent "0.4.2"]
                 ]

  :source-paths ["src/clj"]

  :plugins [[lein-cljsbuild "1.0.3"]
            [lein-figwheel "0.1.3-SNAPSHOT"]
            ]

  :cljsbuild {
              :builds [{ :source-paths ["src/cljs"]
                         :compiler { :output-to "resources/public/js/compiled/mendel.js"
                                     :output-dir "resources/public/js/compiled/out"
                                    :optimizations :none }}]
              }

  :figwheel {
             :http-server-root "public" ;; this will be in resources/
             :port 3449                 ;; default

             ;; CSS reloading
             ;; :css-dirs has no default value
             ;; if :css-dirs is set figwheel will detect css file changes and
             ;; send them to the browser
             :css-dirs ["resources/public/css"]
             }
  )
