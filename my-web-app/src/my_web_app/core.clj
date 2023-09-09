(ns my-web-app.core
  (:require [ring.adapter.jetty :as jetty]
            [compojure.core :refer [defroutes GET POST]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.reload :refer [wrap-reload]]
            [com.nopolabs.clozxing.encode :as encode]
            [com.nopolabs.clozxing.decode :as decode]
            [hiccup.core :refer :all]))

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
       [:li.nav-item [:a {:href "/qrgenerator"} "QR code generator"]]
       [:li.nav-item [:a {:href "/scanner"} "Online scanner"]]]]
     [:div.login-button-container
      [:a.login-button {:href "/login"} "Login"]]]
    [:main content]]))

(defn home-page []
  (render-page "Home"
               ""))

(defn qr-page [request]
  (render-page "QR generator"
               [:div.qr-generator
                [:form
                 {:action "/qr"
                  :method "POST"} 
                 [:label {:for "qr-text"} "Enter text:"]
                 [:input {:type "text"
                          :name "qr-text"
                          :id "qr-text"}]
                 [:button {:type "submit"} "Encode"]
                 ]
                [:div.qr-code-result ;
                 [:img {:src ""
                        :alt "QR Code"
                        :id "qr-code-img"}]]]))


(defn qr-generator [text]
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
                          :href "/css/qradd.css"}]
                  [:h1 "QR code generated"]
                  [:h2 (str "Encode message: " text)]
                  [:img {:src (str "/qrcodes/" unique-filename)
                         :alt "QR Code"}]])))

(defroutes app-routes
  (GET "/home" [] (home-page))
  (GET "/qrgenerator" [re] (qr-page re))
  (GET "/qr/:text" [text] (qr-generator text))
  (POST "/qr" [qrtext] (qr-page qrtext)))
  ;; (GET "/scanner" [] (scanner-page))

(def app
  (-> app-routes
      (wrap-defaults site-defaults)
      (wrap-reload {:dirs ["src"]})))

(defn -main []
  (let [port 3000]
    (jetty/run-jetty app {:port port})
    (println (str "Server started on port " port))))
