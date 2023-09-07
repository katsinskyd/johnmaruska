(ns resume
  (:require
   [clojure.string :as string]
   [hiccup.page :refer [html5]]
   [clojure.edn :as edn]
   [clojure.java.io :as io]))

;; TODO: how to generate a PDF?

(defn head [{:keys [basics]}]
  [:head
   [:meta {:name "description" :content (str (:name basics) " - " (:label basics))}]
   [:meta {:name "keywords" :content (map name (:meta-keywords basics))}]
   [:meta {:name "viewport" :content "initial-scale=1,width=device-width"}]
   [:meta {:content "no-cache" :http-equiv "cache-control"}]
   [:link {:href "styles.css" :rel "stylesheet" :type "text/css"}]
   [:title (str (:name basics) ". " (:label basics))]])

(defn title [{:keys [basics]}]
  [:div.title
   [:h1.name (:name basics)]
   [:p.role (:label basics)]
   [:p.location (:location basics)]])

(def pdf-link
  [:a.pdf-link {:href "resume.pdf"}
   [:img {:src "img/pdf-icon.svg"}]])

(defn contact-links [{:keys [basics]}]
  [:ul.contact-links
   (for [profile (:profiles basics)]
     [:li
      [:a {:href (:url profile)}
       [:img {:src (:icon profile)}]
       (:network profile)]])])

(defn summary [{:keys [basics]}]
  [:div.summary
   [:h2 "Summary"]
   [:p (:summary basics)]])

(defn interval [{:keys [start end]}]
  (if (nil? end)
    (str "Started " start)
    (str start " to " end)))

(defn experience-li [entry]
  [:li.company
   [:div.summary
    [:a.name {:href (:website entry)} (:company entry)]
    ;; TODO: what's the point of the thread-decor-h and hidden-labels? styling and autoscrapers?
    [:div.role (:position entry)]
    [:div.interval (interval entry)]
    [:div.keywords
     [:p (string/join "," (map name (:keywords entry)))]]]
   [:div.details
    [:p (:highlight entry)]
    [:ul (for [bullet (:bullets entry)]
           [:li bullet])]]])

(defn experience [{:keys [fulltime-experience]}]
  [:div.experience
   [:h2 (:title fulltime-experience)]
   [:ul.experience
    (for [entry (:content fulltime-experience)]
      (experience-li entry))]])

(defn education [{:keys [education]}]
  [:div.education
   [:h2 (:title education)]
   [:ul
    (for [entry (:content education)]
      [:li
       [:p (str (:study-type entry) " in " (:area entry))]
       [:p.institution
        [:a {:href (:website entry)} (:institution entry)]]])]])

(defn today [] (.format (java.text.SimpleDateFormat. "MMMM d, YYYY") (java.util.Date.)))
(defn updated []
  [:div.updated-date (str "updated:" (today))])

(defn render [resume-data]
  (html5
   (head resume-data)
   [:body
    pdf-link
    [:div.header
     (title resume-data)
     (contact-links resume-data)]
    (summary resume-data)
    (experience resume-data)
    (education resume-data)
    (updated)]))

(defn page []
  (render (edn/read-string (slurp (io/resource "experience.edn")))))
