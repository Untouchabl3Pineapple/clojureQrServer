(defproject QR-Server "0.1.0-SNAPSHOT"
  :description "QRServer project"
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [ring/ring-defaults "0.3.2"]
                 [ring/ring-core "1.9.0"]
                ;;  [ring.middleware.resource "0.10.0"]
                 [ring/ring-jetty-adapter "1.9.0"]
                 [ring/ring-devel "1.9.0"]
                 [compojure "1.7.0"]
                 [com.nopolabs/clozxing "0.1.1"]
                 [org.clojure/clojurescript "1.11.54"]
                 [hiccup "1.0.5"]
                 [ring/ring-anti-forgery "1.3.0"]
                 [com.github.seancorfield/next.jdbc "1.3.883"]
                 [org.postgresql/postgresql "42.2.10"]]
  :plugins [[lein-cljsbuild "1.1.7"]]
  :cljsbuild
  {:builds
   [{:id "cljs"
     :source-paths ["cljs/src/QR_server"]
     :compiler {:output-to "resources/public/js/scanner.js"
                :optimizations :advanced
                :pretty-print false}}]}
  :main ^:skip-aot QR-Server.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
