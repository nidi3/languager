var __lang = (function () {
    var hiding, langs = [],
        divNode = document.getElementById('__lang'),
        msgNode = document.getElementById('__langMsg'),
        keyNode = document.getElementById('__langKey'),
        dataNode = document.getElementById('__langData'),
        send = function (url, handler) {
            var scriptNode = document.createElement('script');
            scriptNode.src = "http://127.0.0.1:8880/" + url + "?jsonp=__lang." + handler;
            document.body.appendChild(scriptNode);
            document.body.removeChild(scriptNode);
        },
        showMsg = function (msg) {
            msgNode.style.visibility = 'visible';
            msgNode.firstChild.nodeValue = msg;
            setTimeout(function () {
                msgNode.style.visibility = 'hidden';
            }, 1000);
        };


    return {
        hide: function () {
            hiding = setTimeout(function () {
                divNode.style.display = 'none';
            }, 100);
        },
        insideDiv: function () {
            clearTimeout(hiding);
        },
        show: function (event, key) {
            send('value/' + key, 'valuesHandler');
            keyNode.firstChild.nodeValue = key;
            var y = event.pageY;
            var lowerGap = innerHeight + scrollY - y - divNode.offsetHeight;
            if (lowerGap < 0) {
                y += lowerGap;
            }
            divNode.style.top = (event.pageY - 50) + 'px';
            divNode.style.left = event.pageX + 'px';
            divNode.style.display = 'block';
        },
        submit: function () {
            var i, q = {};
            for (i = 0; i < langs.length; i += 1) {
                q[langs[i]] = document.getElementById('__lang-' + langs[i]).value;
            }
            send('setValue/' + keyNode.firstChild.nodeValue + '/' + encodeURIComponent(JSON.stringify(q)), 'submitHandler');
        },
        submitHandler: function (res) {
            if (typeof res === 'string') {
                showMsg(res);
            }
        },
        valuesHandler: function (res) {
            if (typeof res === 'string') {
                showMsg(res);
            } else {
                var lang;
                if (langs.length === 0) {
                    for (lang in res) {
                        var label = document.createElement('div'),
                            labelText = document.createTextNode(lang),
                            area = document.createElement('textarea');
                        label.appendChild(labelText);
                        dataNode.appendChild(label);
                        dataNode.appendChild(area);
                        area.id = '__lang-' + lang;
                        langs.push(lang);
                    }
                }
                for (lang in res) {
                    document.getElementById('__lang-' + lang).value = res[lang];
                }

                var upperGap = divNode.offsetTop - scrollY,
                    lowerGap = innerHeight - divNode.offsetHeight - upperGap,
                    leftGap = divNode.offsetLeft - scrollX,
                    rightGap = innerWidth - divNode.offsetWidth - leftGap;
                if (upperGap < 0) {
                    divNode.style.top = (divNode.offsetTop - upperGap) + 'px';
                }
                if (lowerGap < 0) {
                    divNode.style.top = (divNode.offsetTop + lowerGap) + 'px';
                }
                if (leftGap < 0) {
                    divNode.style.left = (divNode.offsetLeft - leftGap) + 'px';
                }
                if (rightGap < 0) {
                    divNode.style.left = (divNode.offsetLeft + rightGap) + 'px';
                }
            }
        }
    };
}());