(ns cljs-blog.components.floating-label)

(defn label [for text]
  [:label {:for for
           :class [:absolute
                   :left-0
                   :-top-3.5
                   :text-gray-600
                   :text-sm
                   :transition-all
                   "peer-placeholder-shown:text-base"
                   "peer-placeholder-shown:text-gray-400"
                   "peer-placeholder-shown:top-2"
                   "peer-focus:-top-3.5"
                   "peer-focus:text-gray-600"
                   "peer-focus:text-sm"]}
   text])

(defn input [& {:keys [id type name
                       on-change]}]
  [:input {:id id
           :type type
           :name name
           :auto-complete "off"
           :class [:peer :h-10 :w-full :border-b-2 :border-gray-300
                   :text-gray-900 :placeholder-transparent
                   "focus:outline-none"
                   "focus:border-rose-600"]
           :on-change on-change
           :placeholder "not shown"}])

(defn floating-label
  "id: id attribute of input tag
   type: password | text | number
   name: name attribute of input tag
   text: label text"
  [& {:keys [id type name text
             on-change]}]
  [:div.mt-10.relative
   [input {:id id
           :type type
           :name name
           :on-change on-change}]
   [label id text]])