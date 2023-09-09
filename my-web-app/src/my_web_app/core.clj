(ns my-web-app.core
  (:require [ring.adapter.jetty :as jetty]
            [compojure.core :refer [defroutes GET POST]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.reload :refer [wrap-reload]]
            [com.nopolabs.clozxing.encode :as encode]
            [com.nopolabs.clozxing.decode :as decode]
            [hiccup.core :refer :all]
            [ring.util.anti-forgery :refer [anti-forgery-field]]
            [ring.util.response :refer [response]]))

;; TODO: make a multi-file project

(defn render-page [title content]
  (hiccup.core/html
   [:head
    [:title title]
    [:link {:rel "stylesheet"
            :type "text/css"
            :href "/css/header.css"}]]
   [:body
    [:header.header
     [:nav
      [:ul.nav-list
       [:li.nav-item [:a {:href "/home"} "Home"]]
       [:li.nav-item [:a {:href "/scanner"} "Online scanner"]]
       [:li.nav-item [:a {:href "/qrgenerator"} "QR code generator"]]
       [:li.nav-item [:a {:href "/logger"} "Logger"]]]]
     [:div.login-button-container
      [:a.login-button {:href "/login"} "Login"]]]
    [:main content]]))

(defn home-page []
  (render-page "Home"
               ""))

;; (use 'ring.util.anti-forgery)

(defn qr-page []
  (render-page "QR generator"
               [:div.qr-generator
                [:link {:rel "stylesheet"
                        :type "text/css"
                        :href "/css/qrgen.css"}]
                [:form
                 {:action "/qrgenerator"
                  :method "POST"}
                 (anti-forgery-field)
                 [:label {:for "qr-text"} "Input message to encode"]
                 [:input {:type "text"
                          :name "text"
                          :id "qr-text"}]
                 [:button {:type "submit"} "Encode"]]]))

(defn qr-generator [text]
  (if (empty? text)
    (qr-page)
    (let [unique-filename (str (java.util.UUID/randomUUID) ".png")
          file-path (str "resources/public/qrcodes/" unique-filename)]
      (encode/to-file
       text
       file-path
       {:size 200
        :error-correction encode/error-correction-H
        :character-set encode/iso-8859-1
        :margin 1
        :format "PNG"})

      (render-page "QR Code Page"
                   [:main
                    [:link {:rel "stylesheet"
                            :type "text/css"
                            :href "/css/qrres.css"}]
                    [:h1 "QR code generated"]
                    [:h2 (str "Encode message: " text)]
                    [:img {:src (str "/qrcodes/" unique-filename)
                           :alt "QR Code"}]]))))


(defroutes app-routes
  (GET "/home" [] (home-page))
  (GET "/qrgenerator" [] (qr-page))
  (POST "/qrgenerator" [text] (qr-generator text)))
  ;; (GET "/qrgenerator/:text" [text] (qr-generator text)))
  ;; (GET "/scanner" [] (scanner-page))


(def app
  (-> app-routes
      (wrap-defaults site-defaults)
      (wrap-reload {:dirs ["src"]})))

(defn -main []
  (let [port 3000]
    (jetty/run-jetty app {:port port})
    (println (str "Server started on port " port))))
