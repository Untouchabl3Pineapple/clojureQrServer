(ns QR-Server.handlers
  (:require
   [com.nopolabs.clozxing.encode :as encode]
   [hiccup.core :refer :all]
   [clojure.java.io :as io]
   [QR-Server.pages :as pages]
   [QR-Server.auth :as cas]
   [QR-Server.db :as db]))


(defn home-gethandler []
  (pages/home-page))


(defn scanner-gethandler []
  (pages/scanner-page))


(defn logger-gethandler []
  (if (cas/verify-admin-token (str (deref cas/global-token)))
    (let [db (db/get-connection)
          logs (db/get-all-logs db)]
      (pages/logger-page logs))
    (cas/illegal-token)))


(defn log-posthandler [requestData]
  (let [db (db/get-connection)]
    (db/insert-log db requestData)))


(defn qr-generator-gethandler []
  (pages/qr-page))


(defn qr-generator-posthandler [requestData]
  (let [qrcodes-dir "resources/public/qrcodes"]
    (if (not (.exists (io/file qrcodes-dir)))
      (.mkdirs (io/file qrcodes-dir))))

  (if (empty? requestData)
    (pages/qr-page)
    (let [unique-filename (str (java.util.UUID/randomUUID) ".png")
          file-path (str "resources/public/qrcodes/" unique-filename)]
      (encode/to-file
       requestData
       file-path
       {:size 200
        :error-correction encode/error-correction-H
        :character-set encode/iso-8859-1
        :margin 1
        :format "PNG"})
      (pages/qr-generator-page unique-filename requestData))))


(defn login-gethandler []
  (if (= 1 1)
    (cas/reset-all))
  (pages/login-page))


(defn login-posthandler []
  cas/cas-auth)


(defn logout-gethandler []
  (if (= 1 1)
    (cas/reset-all))
  (cas/redirect "/home" 302))