(ns trycypher.views.home
  (:require [noir.core :refer [defpartial defpage]]
            [hiccup.element :refer [javascript-tag link-to unordered-list]]
            [hiccup.page :refer [include-css include-js html5]]))

(defpartial links []
  (unordered-list
   [(link-to "http://neo4j.org" "The official Neo4j website")
    (link-to "http://groups.google.com/group/neo4j" "Neo4j mailing list")
    (link-to "http://www.manning.com/partner/" "The Neo4j in Action book by Jonas Partner and Aleksa Vukotic")
    ]))

(defpartial about-html []
  [:p.bottom
   "Please note that this console is working against a live remote database. "
   "Keep in mind that this site is designed for "
   "beginners to try out Cypher and not necessarily as a general-purpose server-side console."]
  [:p.bottom
   "You can find the site's source and such on its "
   (link-to "http://github.com/jexp/trycypher" "github")
   " page."]
  [:p.bottom
   "TryClojure is written in Clojure and JavaScript (JQuery), powered by "
   (link-to "https://github.com/michaelklishin/neocons" "neocons")
   " and Chris Done's "
   (link-to "https://github.com/chrisdone/jquery-console" "jquery-console")
   " it builds upon the " 
    (link-to "http://github.com/Raynes/tryclojure" "TryClojure console")
    " and replaced the clojure evaluation by cypher execution"
   ]
  [:p.bottom "Design by " (link-to "http://apgwoz.com" "Andrew Gwozdziewycz")])

(defpartial home-html []
  [:p.bottom
   "Welcome to Try Cypher. See that little box up there? That's a Cypher console. You can type "
   "statements and see their results right here in your browser. We also have a brief tutorial to "
   "give you a taste of Cypher. Try it out by typing " [:code.expr "tutorial"] " in the console!"]
  [:p.bottom
   "Check out the site's source on "
   (link-to "http://github.com/jexp/trycypher" "github")
   "!"])

(defn root-html []
  (html5
   [:head
    (include-css "/resources/public/css/trycypher.css")
    (include-js "http://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"
                "/resources/public/javascript/jquery-console/jquery.console.js"
                "/resources/public/javascript/trycypher.js")
    [:title "Try Cypher"]]
   [:body
    [:div#wrapper
     [:div#content
      [:div#header
       [:h1
        [:span.logo-try "Try"] " "
        [:span.logo-clojure "Cyp" [:em ""] "her"]]]
      [:div#container
       [:div#console.console]
       [:div#buttons
        [:a#home.buttons "home"]
        [:a#links.buttons "links"]
        [:a#about.buttons.last "about"]]
       [:div#changer (home-html)]]
      [:div.footer
       [:p.bottom "©2012 Michael Hunger, Anthony Grimes and numerous contributors"]
       [:p.bottom
        "Built with "
        (link-to "http://webnoir.org" "Noir")
        "."]]
      (javascript-tag
       "var _gaq = _gaq || [];
        _gaq.push(['_setAccount', 'UA-1192232-25']);
        _gaq.push(['_trackPageview']);

        (function() {
          var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
          ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
          var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
        })();")]]]))

(defpage "/" []
  (root-html))

(defpage "/home" []
  (home-html))

(defpage "/about" []
  (about-html))

(defpage "/links" []
  (links))
