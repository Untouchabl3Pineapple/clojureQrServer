(ns QR-Server.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.reload :refer [wrap-reload]]
            [hiccup.core :refer :all]
            [QR-Server.routes :as routes]))


(def app
  (-> routes/app-routes
      (wrap-defaults site-defaults)
      (wrap-reload {:dirs ["src"]})))


(defn -main []
  (let [port 3000]
    (jetty/run-jetty app {:port port})))

