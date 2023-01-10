(ns cljs-blog.key-tree)

(defrecord Path [tag v k parent-path siblings])

(def top (map->Path {:tag :top}))

(defn is-path-top? [path]
  (= (:tag path) :top))

(defrecord Location [tree path])

(defrecord KeyTree [value key-tree-children])

(defn create-node
  "create a key-tree with value"
  [value]
  (map->KeyTree {:value value
                 :key-tree-children {}}))

(defn- set-tree
  "set the whole tree in this location
   t: the new tree
   loc: current location"
  [t loc]
  (assoc loc :tree t))

(defn- update-tree
  "update the tree in this location using f
   f: function apply to tree
   loc: current location"
  [f loc]
  (let [tree (:tree loc)]
    (assoc loc :tree (f tree))))

(defn change
  "change the whole tree in this location"
  [t loc]
  (assoc loc :tree t))

(defn change-value
  "change the tree's current node value in this location"
  [f old-location]
  (let [old-tree (:tree old-location)
        old-value (:value old-tree)
        new-value (f old-value)
        new-tree (assoc old-tree :value new-value)]
    (change new-tree old-location)))

(defn set-value
  [new-value old-location]
  (let [old-tree (:tree old-location)
        old-value (:value old-tree)
        new-tree (assoc old-tree :value new-value)]
    (change new-tree old-location)))

(defn get-value
  [location]
  (let [tree (:tree location)
        value (:value tree)]
    value))

(defn update-value
  "change the tree's current node value in this location"
  [f old-location]
  (let [old-tree (:tree old-location)
        old-value (:value old-tree)
        new-value (f old-value)
        new-tree (assoc old-tree :value new-value)]
    (set-tree new-tree old-location)))

;; TODO: spec 他的输入类型是 location
(defn go-up [loc]
  (let [t (:tree loc)
        path (:path loc)
        {v :v k :k up :parent-path actions :siblings} path
        new-tree (map->KeyTree {:value v
                                :key-tree-children
                                (assoc actions k t)})]
    (map->Location {:tree new-tree :path up})))

;; TODO: spec
(defn go-down
  "go to key 'k' of location 'loc'"
  ([k loc]
   (let [{:keys [tree path]} loc
         {v :value actions :key-tree-children} tree
         t (actions k)]
     (when t
       (map->Location
        {:tree t
         :path (map->Path {:tag :non-top
                           :v v
                           :k k
                           :parent-path path
                           :siblings (dissoc actions k)})}))))
  ([k new-value loc]
   (let [{:keys [tree path]} loc
         {v :value actions :key-tree-children} tree]
     (map->Location
      {:tree (create-node new-value)
       :path (map->Path {:tag :non-top
                         :v v
                         :k k
                         :parent-path path
                         :siblings (dissoc actions k)})}))))

;; TODO: spec
(defn up-to-root [loc]
  (if (is-path-top? (:path loc))
    loc
    (up-to-root (go-up loc))))

(defn- map-vals [f m]
  (into {} (for [[k v] m]
             [k (f v)])))

(defn- expand-tree-one-step [moves key-tree]
  (let [children (:key-tree-children key-tree)
        value (:value key-tree)]
    (if (empty? children)
      (assoc key-tree
             :key-tree-children
             (map-vals #(map->KeyTree {:value % :key-tree-children {}}) (moves value)))
      (assoc key-tree
             :key-tree-children
             (map-vals #(expand-tree-one-step moves %) children)))))

(defn add-child
  "add a child to current location's children"
  [key value loc]
  (let [key-tree (:tree loc)
        key-tree-children (:key-tree-children key-tree)
        new-children (assoc key-tree-children key (create-node value))
        new-tree (assoc key-tree :key-tree-children new-children)]
    (set-tree new-tree loc)))

(defn expand-one-step 
  "moves: a value level function for how to transition to next level
   e.g. (fn [old-value] {:key1 :child1 :key2 :child2})
   location: current location"
  [moves location]
  (let [key-tree (:tree location)
        new-tree (expand-tree-one-step moves key-tree)]
    (set-tree new-tree location)))

(defn get-max-key [key-tree]
  (first (:key-tree-children key-tree)))

(defn descend
  "使用ucb从root开始往下descend, 找到一个新节点
   return: Leaf node"
  [location is-max-node moves]
  (let [tree (:tree location)
        children (:key-tree-children tree)]
    (if (empty? children)
      (let [new-tree (expand-tree-one-step moves tree)
            k (first (keys (:key-tree-children new-tree)))]
        (aset js/window "tree" tree)
        (aset js/window "new-tree" new-tree)
        (go-down k new-tree location))
      (let [max-key (get-max-key tree)
            max-child (go-down max-key location)]
        (println (str "max-key" max-key))
        (descend max-child (not is-max-node) moves)))))

(comment
  ;; create root with value
  (def root (create-node :root))
  ;; the top path
  top
  ;; top path together with root create a Location
  (def loc (map->Location {:tree root :path top}))
  ;; get value from root
  (get-value loc)

  ;; set a new value in root node
  (def new-loc (set-value :new-root loc))
  ;; get value in new-loc
  (get-value new-loc)

  ;; update a new value in root
  (def new-loc1 (change-value #(str % "new-value") loc))
  (get-value new-loc1)

  ;; add a child to loc
  (def added-child (add-child :key :value loc))
  (get-value (go-down :key added-child))

  ;; move is just a value level function
  ;; given one node value, return all it children
  (defn moves [root-value]
    {:key1 :child1
     :key2 :child2})
  (moves :root)

  ;; lift to tree-level
  (def level-2-loc (expand-one-step moves loc))
  (get-value level-2-loc)
  ;; tree can go down
  (def child1-loc (go-down :key1 level-2-loc))
  (def child2-loc (go-down :key2 level-2-loc))
  (get-value child1-loc)
  (get-value child2-loc)

  ;; end
  )