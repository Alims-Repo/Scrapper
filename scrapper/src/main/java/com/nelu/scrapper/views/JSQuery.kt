package com.nelu.scrapper.views

object JSQuery {

    const val REMOVE_HEADER = "(function() { " +
            "var headerElements = document.getElementsByClassName('css-d03obp-DivHeaderContainer');" +
            "if (headerElements.length > 0) {" +
            "headerElements[0].remove();" +
            "}" +
            "})();"

    const val REMOVE_MODAL = "(function() { " +
            "var modalElements = document.getElementsByClassName('css-py8jux-DivModalContainer');" +
            "if (modalElements.length > 0) {" +
            "modalElements[0].remove();" +
            "}" +
            "})();"

    const val REMOVE_CONTAINER = "(function() { " +
            "var containerElements = document.getElementsByClassName('css-hsy0fo-DivContainer');" +
            "if (containerElements.length > 0) {" +
            "containerElements[0].remove();" +
            "}" +
            "})();"

    /**
     * Removing button doesn't allow share and download latter
     *
    const val REMOVE_BUTTON = "(function() { " +
            "var elements = document.getElementsByClassName('e365p2r5');" +
            "for (var i = 0; i < elements.length; i++) {" +
            "elements[i].remove();" +
            "}" +
            "})();"

    */


    const val CLICK_SHARE = "(function() {" +
            "    var iconElements = document.querySelectorAll('[data-e2e=\"share-icon\"]');" +
            "    if (iconElements.length > 3) {" +
            "        iconElements[1].click();" +
            "    } else if (iconElements.length > 1) {" +
            "        iconElements[0].click();" +
            "    }" +
            "})();"

    const val CLICK_COPY = "(function() { " +
            "var iconElement = document.querySelector('.css-1bhic2r-DivPlatWrap');" +
            "if (iconElement) {" +
            "   iconElement.click();" +
            "}" +
            "})();"

    const val HIDE_COPY_DIALOG = "(function() { " +
            "var elements = document.getElementsByClassName('css-14feuhu');" +
            "for (var i = 0; i < elements.length; i++) {" +
            "elements[i].style.display = 'none';" +
            "}" +
            "})();"

    const val HIDE_BUTTON = "(function() { " +
            "var elements = document.getElementsByClassName('e365p2r4');" +
            "for (var i = 0; i < elements.length; i++) {" +
            "elements[i].style.display = 'none';" +
            "}" +
            "})();"

    const val REMOVE_FOOTER = "(function() { " +
            "var footerElements = document.getElementsByClassName('css-11qhbzg-FooterContainer');" +
            "if (footerElements.length > 0) {" +
            "footerElements[0].remove();" +
            "}" +
            "})();"

    const val GET_USER_ID = "(function() { " +
            "var hrefValues = [];" +
            "var elements = document.querySelectorAll('h2.css-il1zfv-H2UserName a');" +
            "for (var i = 0; i < elements.length; i++) {" +
            "hrefValues.push(elements[i].getAttribute('href'));" +
            "}" +
            "return JSON.stringify(hrefValues);" +
            "})();"
}