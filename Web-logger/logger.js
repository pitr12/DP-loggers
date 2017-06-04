/**
 * Created by Peter Dubec on 02.12.2016.
 */


var Logger = (function (){
    var topics = {};
    var hasProp = topics.hasOwnProperty;

    return {
        init: function() {
            var rootElement = document.documentElement;
            rootElement.addEventListener('click', this.clickListener.bind(this));
            // window.addEventListener('scroll', this.scrollListener.bind(this));
            // rootElement.addEventListener('scroll', this.scrollListener.bind(this));
            // window.addScrollListener(this.scrollListener.bind(this));
            // $(window).scroll(function(){
            //   console.log('SCROLL BODY');
            // });
        },
        getPathTo: function(element) {
            if (element.id!=='')
                return "//*[@id='"+element.id+"']";

            if (element===document.body)
                return element.tagName.toLowerCase();

            var count= 0;
            var elementSiblings= element.parentNode.childNodes;
            for (var i= 0; i<elementSiblings.length; i++) {
                var elementSibling= elementSiblings[i];

                if (elementSibling===element) return this.getPathTo(element.parentNode) + '/' + element.tagName.toLowerCase() + '[' + (count + 1) + ']';

                if (elementSibling.nodeType===1 && elementSibling.tagName === element.tagName) {
                    count++;
                }
            }
        },
        processEvent: function (event) {
            var newEvent;

            if(typeof(String.prototype.trim) === "undefined")
            {
                String.prototype.trim = function()
                {
                    return String(this).replace(/^\s+|\s+$/g, '');
                };
            }

            switch(event.type) {

                case 'click':
                    newEvent = {
                        button: event.button,
                        buttons: event.buttons,
                        clientX: event.clientX,
                        clientY: event.clientY,
                        pageX: event.pageX,
                        pageY: event.pageY,
                        screenX: event.screenX,
                        screenY: event.screenY,
                        target: {
                            className: event.target.className,
                            id: event.target.id,
                            name: event.target.name,
                            title: event.target.title,
                            type: event.target.type,
                            value: event.target.value || event.target.textContent.trim(),
                            tagName: event.target.tagName,
                            targetHeight: event.target.clientHeight,
                            targetWidth: event.target.clientWidth,
                            path: this.getPathTo(event.target)
                        },
                        type: event.type,
                        timestamp: Date.now(),
                        windowHeight: window.innerHeight,
                        windowWidth: window.innerWidth,
                        screenHeight: screen.height,
                        screenWidth: screen.width
                    };
                    break;

                case 'scroll':
                    newEvent = {
                        scrollY: window.scrollY,
                        type: event.type,
                        timestamp: Date.now(),
                        windowHeight: window.innerHeight,
                        windowWidth: window.innerWidth,
                        screenHeight: screen.height,
                        screenWidth: screen.width
                    };
                    break;

                default:
                    return event;
            }

            return newEvent;

        },
        clickListener: function (e) {
            var event = this.processEvent(e);
            this.publish('click', event);
        },
        scrollListener: function (e) {
            var event = this.processEvent(e);
            this.publish('scroll', event);
        },
        subscribe: function(topic, topicCallback) {
          if(!hasProp.call(topics, topic)) topics[topic] = [];

          var i = topics[topic].push(topicCallback) -1;

          return {
            remove: function() {
              delete topics[topic][i];
            }
          };
        },
        publish: function(topic, content) {
          if(!hasProp.call(topics, topic)) return;

          topics[topic].forEach(function(topicCallback) {
                topicCallback(content != undefined ? content : {});
          });
        }
    }
})();
