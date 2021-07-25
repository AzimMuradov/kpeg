let kdoc_nav_item = document
    .querySelector(".md-nav__list:first-of-type")
    .lastElementChild;

let kdoc_nav_link = kdoc_nav_item.firstElementChild;


// Set styles
kdoc_nav_item.setAttribute("class", kdoc_nav_item.className + " kdoc_nav_item");

// Open in the new tab
kdoc_nav_link.setAttribute("target", "_blank");
