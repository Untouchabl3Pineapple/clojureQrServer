(ns my-web-app.core)

(def video (.getElementById js/document "qr-video"))
(def qrResult (.getElementById js/document "qr-result"))
(def antiForgeryToken (.-value (js/document.querySelector "#__anti-forgery-token")))
(def prevQRCode (atom nil)) ; Атом для хранения предыдущего QR-кода

(defn handleQRCodeScan
  [result]
  (js/console.log antiForgeryToken)
  (js/fetch "/hi"
            #js
             {:method "POST"
              :headers #js {"Content-Type" "application/x-www-form-urlencoded", "X-CSRF-Token" antiForgeryToken}
              :body (js/encodeURI (str "qrData=" result))})
  .then
  (fn [response]
    (.json response))
  .then
  (fn [data]
    (js/console.log "Данные успешно отправлены на сервер:", data))
  .catch
  (fn [error]
    (js/console.error "Ошибка при отправке данных на сервер:", error)))

(defn tick
  []
  (when (= (.-readyState video) (.-HAVE_ENOUGH_DATA video))
    ;; (set! (.-textContent qrResult) "Scanning...")
    (def canvasElement (.createElement js/document "canvas"))
    (def canvas (.getContext canvasElement "2d"))
    (set! (.-width canvasElement) (.-videoWidth video))
    (set! (.-height canvasElement) (.-videoHeight video))
    (.drawImage
     canvas
     video
     0
     0
     (.-width canvasElement)
     (.-height canvasElement))
    (def imageData
      (.getImageData
       canvas
       0
       0
       (.-width canvasElement)
       (.-height canvasElement)))
    (def code
      (js/jsQR
       (.-data imageData)
       (.-width imageData)
       (.-height imageData)
       #js {:inversionAttempts "dontInvert"}))
    (if (and code (not= "" (.-data code)) (not= @prevQRCode (.-data code)))
      (do
        (set! (.-textContent qrResult) (str "" (.-data code)))
        (handleQRCodeScan (.-data code))
        (reset! prevQRCode (.-data code))))) ; Обновляем предыдущее значение QR-кода
      ;; (set! (.-textContent qrResult) "QR code not found")))
  (js/requestAnimationFrame tick))

(.then
 (.getUserMedia
  (.-mediaDevices js/navigator)
  #js {:video #js {:facingMode "environment"}})
 (fn [stream]
   (set! (.-srcObject video) stream)
   (.setAttribute video "playsinline" true)
   (.play video)
   (js/requestAnimationFrame tick)))
