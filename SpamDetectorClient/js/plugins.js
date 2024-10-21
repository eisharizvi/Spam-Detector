// Avoid `console` errors in browsers that lack a console.
(function() {
  var method;
  var noop = function () {};
  var methods = [
    'assert', 'clear', 'count', 'debug', 'dir', 'dirxml', 'error',
    'exception', 'group', 'groupCollapsed', 'groupEnd', 'info', 'log',
    'markTimeline', 'profile', 'profileEnd', 'table', 'time', 'timeEnd',
    'timeline', 'timelineEnd', 'timeStamp', 'trace', 'warn'
  ];
  var length = methods.length;
  var console = (window.console = window.console || {});

  while (length--) {
    method = methods[length];

    // Only stub undefined methods.
    if (!console[method]) {
      console[method] = noop;
    }
  }
}());

// Place any jQuery/helper plugins in here.
// Fetch messages from the server
// fetch('yourServerURL/messages')
//   .then(response => response.json())
//   .then(messages => {
//     messages.forEach(message => {
//       if (isSpam(message.text)) {
//         console.log('Spam detected:', message.text);
//       } else {
//         console.log('Not spam:', message.text);
//       }
//     });
//   })
//   .catch(error => console.error('Error:', error));

// // Send a message to the server
// function sendMessage(text) {
//   fetch('http://localhost:8080/spamDetector-1.0/api/spam', {
//     method: 'POST',
//     headers: {
//       'Content-Type': 'application/json',
//     },
//     body: JSON.stringify({ text }),
//   })
//     .then(response => response.json())
//     .then(data => console.log('Message sent:', data))
//     .catch(error => console.error('Error:', error));
// }
