package com.nelu.scrapper.config

object JSQuery {

    const val TIKTOK_AUTO_PLAY = "javascript:document.querySelector('css-12jr7p2-DivPlayerIconContainer').click();"

    const val REMOVE_LOGIN = """
                                    (function() {
                                        var button = document.querySelector('.css-u3m0da-DivBoxContainer');
                                        if (button) {
                                            button.click();
                                        } else {
                                            console.error('Button not found');
                                        }
                                    })();
                                """

    const val GET_PROFILE_DATA = """
                                    (function() {
                                        var elements = document.querySelectorAll('.css-x6y88p-DivItemContainerV2');
                                        var data = [];
                                        elements.forEach(function(element) {
                                            var aTag = element.querySelector('a');
                                            var alt = aTag.querySelector('img').getAttribute('alt');
                                            var href = aTag.getAttribute('href');
                                            var sourceTag = element.querySelector('source');
                                            var src = sourceTag ? sourceTag.getAttribute('src') : null;
                                            var obj = { 'alt': alt, 'href': href, 'src': src };
                                            data.push(obj);
                                        });
                                        return data; //JSON.stringify(data);
                                    })();
                                    """
}