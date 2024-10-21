// Import the natural library
// const natural = require('natural');
// const classifier = new natural.BayesClassifier();

window.addEventListener('load', () => {
  load_spam_data();
  load_precision();
  load_accuracy();
});

function load_precision() {
  let url = "http://localhost:8080/spamDetector-1.0/api/spam/precision";

  fetch(url)
    .then(response => {
      if(!response.ok) {
        throw new Error("Couldn't fetch");
      }
      return response.json();
    })
    .then(data => {
      console.log(`Loaded data from ${url}`, data);
      document.getElementById("precision").innerHTML += data?.val;
    })
    .catch(error => {
      console.error(error);
    });
}

function load_accuracy() {
  let url = "http://localhost:8080/spamDetector-1.0/api/spam/accuracy";

  fetch(url)
    .then(response => {
      if(!response.ok) {
        throw new Error("Couldn't fetch");
      }
      return response.json();
    })
    .then(data => {
      console.log(`Loaded data from ${url}`, data);
      document.getElementById("accuracy").innerHTML += data?.val;
    })
    .catch(error => {
      console.error(error);
    });
}

function load_spam_data() {
  let url = "http://localhost:8080/spamDetector-1.0/api/spam/";

  fetch(url)
    .then(response => {
      if(!response.ok) {
        throw new Error("Couldn't fetch");
      }
      return response.json();
    })
    .then(data => {
      console.log(`Loaded data from ${url}`, data);
      populateUI(data);
    })
    .catch(error => {
      console.error(error);
    });
}

// function will update table based on data retrieved
function populateUI(data) {
  // table id
  let table = document.getElementById('spamTable');

  // add new data to table
  data.forEach(item => {
    const file_td = `<td>${item?.file}</td>`;
    const spam_probability_td = `<td>${item?.spamProbability?.toFixed(20)}</td>`;
    const class_td = `<td>${item?.actualClass}</td>`;

    let row = document.createElement('tr');
    row.innerHTML = `${file_td}${spam_probability_td}${class_td}`;

    const tbody = document.getElementById("tbody");
    tbody.appendChild(row);
    // let textCell = document.createElement('td');
    // let labelCell = document.createElement('td');

    // textCell.textContent = item.text;
    // labelCell.textContent = item.label;

    // row.appendChild(textCell);
    // row.appendChild(labelCell);
    // table.appendChild(row);
  });
}

// // function is responsible for training naive bayes classifier
// function trainClassifier(data) {
//   // Assuming your data is an array of objects with 'text' and 'label' properties
//   data.forEach(item => {
//     classifier.addDocument(item.text, item.label);
//   });

//   classifier.train();
// }

// function isSpam(message) {
//   const label = classifier.classify(message);
//   return label === 'spam';
// }
