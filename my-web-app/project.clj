(defproject my-web-app "0.1.0-SNAPSHOT"
  :description "My Clojure web app"
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [ring/ring-defaults "0.3.2"]
                 [ring/ring-core "1.9.0"]
                 [ring/ring-jetty-adapter "1.9.0"]
                 [ring/ring-devel "1.9.0"]
                 [compojure "1.7.0"]
                 [com.nopolabs/clozxing "0.1.1"]]
  :main ^:skip-aot my-web-app.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
