(ns QR-Server.handlers
  (:require
   [com.nopolabs.clozxing.encode :as encode]
   [hiccup.core :refer :all]
   [clojure.java.io :as io]
   [QR-Server.pages :as pages]
   [QR-Server.auth :as cas]
   [QR-Server.db :as db]))


(defn logger-handler [req]
  (if (cas/verify-token (str (deref cas/global-token)))
    (let [db (db/get-connection)
          logs (db/get-all-logs db)]
      (pages/logger-page logs))
    (cas/illegal-token)))


(defn ensure-qrcodes-directory []
  (let [qrcodes-dir "resources/public/qrcodes"]
    (if (not (.exists (io/file qrcodes-dir)))
      (.mkdirs (io/file qrcodes-dir)))))


(defn qr-generator [text]
  (if (empty? text)
    (pages/qr-page)
    (let [unique-filename (str (java.util.UUID/randomUUID) ".png")
          file-path (str "resources/public/qrcodes/" unique-filename)]
      (ensure-qrcodes-directory)
      (encode/to-file
       text
       file-path
       {:size 200
        :error-correction encode/error-correction-H
        :character-set encode/iso-8859-1
        :margin 1
        :format "PNG"})

      (pages/qr-generator-page unique-filename text))))


(defn home-handler [request]
  (pages/home-page))


(defn login-handler [request]
  (if (= 1 1)
    (cas/reset-all))
  (pages/layout (pages/login-page)))


(defn logout-handler [request]
  (if (= 1 1)
    (cas/reset-all))
  (cas/redirect "/home" 302))