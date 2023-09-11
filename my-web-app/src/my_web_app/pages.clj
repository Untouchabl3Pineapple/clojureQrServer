(ns my-web-app.pages
(:require 
          
           [hiccup.core :refer :all]
           [ring.util.anti-forgery :refer [anti-forgery-field]] 
           [my-web-app.cas :as cas]
           ))
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
       [:li.nav-item [:a {:href "/scanner"} "QR scanner"]]
       [:li.nav-item [:a {:href "/qrgenerator"} "QR code generator"]]
       [:li.nav-item [:a {:href "/logger"} "Logger"]]]]
     [:div.login-button-container
      [:a.login-button {:href "/login"} (deref cas/global-log-in-out)]]]
    [:main content]]))

(defn scanner-page []
  (render-page "Online scanner"
               [:main
                [:link {:rel "stylesheet"
                        :type "text/css"
                        :href "/css/scanner.css"}]
                [:body
                 [:h1 "Show your QR code"]
                 [:video {:id "qr-video" :width 300 :height 300 :autoplay true}]
                 [:p {:id "qr-result"} "QR Code value will appear here after scanning"]
                 [:script {:src "https://cdn.rawgit.com/cozmo/jsQR/master/dist/jsQR.js"}]
                 [:script {:src "/js/app.js" :type "text/javascript"}]]]))

(defn logger-page [logs] 
    (render-page "Logger"
                 [:div
                  [:link {:rel "stylesheet"
                          :type "text/css"
                          :href "/css/logger.css"}]
                  [:table
                   [:thead
                    [:tr
                     [:th "ID"]
                     [:th "Date"]
                     [:th "Data"]]]
                   [:tbody
                    (for [log logs]
                      [:tr
                       [:td (:log_id log)]
                       [:td (:log_date_time log)]
                       [:td (:log_data log)]])]]]))

(defn qr-generator-page [unique-filename text] 
      (render-page "QR Code Page"
                   [:main
                    [:link {:rel "stylesheet"
                            :type "text/css"
                            :href "/css/qrres.css"}]
                    [:h1 "QR code generated"]

                    [:img {:src (str "/qrcodes/" unique-filename)
                           :alt "QR Code"}]
                    [:h2 (str "Encode message: " text)]]))

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

(defn home-page []
  (render-page "QR Code Encoder & Decoder"
                   [:div.container
                    [:link {:rel "stylesheet"
                            :type "text/css"
                            :href "/css/home.css"}]
                    [:h1 "Welcome to the QR Code Encoder & Decoder"]
                    [:p "Encode and decode QR codes online with ease."]
                    [:div.instructions
                     [:h2 "How to Use"]
                     [:p "To encode a message into a QR code, visit the 'Encode' page."]
                     [:p "To decode a QR code and retrieve the original message, visit the 'Decode' page."]]
                    [:div.get-started
                     [:a {:href "/qrgenerator" :class "button"} "Encode QR Code"]
                     [:a {:href "/scanner" :class "button"} "Decode QR Code"]]]))

(defn layout [content]
  (hiccup.core/html
   [:html
    [:head [:title "Authentication Example"]]
    [:body content]]))

(defn login-page []
  (hiccup.core/html
   [:h2 "Login"]
   [:form {:method "POST" :action "/login"}
    (anti-forgery-field)
    [:label {:for "login"} "login: "]
    [:input {:type "text" :name "login"}]
    [:br]
    [:label {:for "password"} "password: "]
    [:input {:type "password" :name "password"}]
    [:br]
    [:input {:type "submit" :value "login"}]]))
