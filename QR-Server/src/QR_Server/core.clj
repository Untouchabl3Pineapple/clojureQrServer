(ns QR-Server.core
  (:require [ring.adapter.jetty :as jetty]
            [compojure.core :refer [defroutes GET POST]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.reload :refer [wrap-reload]]
            [com.nopolabs.clozxing.encode :as encode]
            [hiccup.core :refer :all]
            [QR-Server.pages :as pages]
            [QR-Server.auth :as cas]
            [QR-Server.db :as db]
            [QR-Server.routes :as routes]))

;; TODO: make a multi-file project



(def app
  (-> routes/app-routes
      (wrap-defaults site-defaults)
      (wrap-reload {:dirs ["src"]})))

(defn -main []
  (let [port 3000]
    (jetty/run-jetty app {:port port})))

