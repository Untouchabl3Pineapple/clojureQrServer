(ns my-web-app.core
  (:require [ring.adapter.jetty :as jetty]
            [compojure.core :refer [defroutes GET]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.reload :refer [wrap-reload]]
            [com.nopolabs.clozxing.encode :as encode] ; Пространство имён для кодирования
            [com.nopolabs.clozxing.decode :as decode]))

(defn handle-request [text]
  (let [unique-filename (str (java.util.UUID/randomUUID) ".png")
        file-path (str "img/" unique-filename)]
    (encode/to-file
     text
     file-path
     {:size 200
      :error-correction encode/error-correction-H
      :character-set encode/iso-8859-1
      :margin 1
      :format "PNG"})

    {:status 200
     :headers {"Content-Type" "text/html"}
     :body (str "<html><head><title>QR Code Page</title></head><body><h1>QR Code Generated</h1><img src=\"/img/" unique-filename "\" alt=\"QR Code\"></body></html>")}))


(defroutes app-routes
  (GET "/" [] "Hello")
  (GET "/qr/:text" [text] (handle-request text)))

(def app
  (-> app-routes
      (wrap-defaults site-defaults)
      (wrap-reload {:dirs ["src"]})))

(defn -main []
  (let [port 3000]
    (jetty/run-jetty app {:port port})
    (println (str "Server started on port " port))))
