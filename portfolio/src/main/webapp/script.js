/**
 * Delete from datastore
 */
async function deleteData() {
  fetch('/delete-data', {method:'POST'}).then(() => getData());
}

/**
 * Fetch json practice, array of messages
 */
function getData(maxLoad) {
  fetch('/data?max=' + maxLoad).then(response => response.json()).then(messages => {
    const messagesList = document.getElementById('messages-list');
    messagesList.innerHTML = '';
    for (var i = 0; i < messages.length; i++) {
      messagesList.appendChild(createListElement(messages[i]));
    } 
  });
}

/**
 * Helper to create <li> element
 */
function createListElement(text) {
  const li = document.createElement('li');
  li.innerText = text;
  return li;
}

let lastRandomIndex;

const facts = [
    'I love spicy food, but I dislike eating peppers!',
    'I play the flute!',
    'I have been taking lots of walks during quarantine',
    'I am 19 years old!',
  ];

/**
 * Adds a random fact about myself!
 */
function genRandomFact() {
  let randomIndex = randomNumGenerator(facts.length);
  lastRandomIndex = randomIndex;
  let fact = facts[randomIndex];

  const factContainer = document.getElementById('fact-container');
  factContainer.innerText = fact;
}

/**
 * Generates random number from 0 to parameter factsLength, no repeat
 * @param factsLength
 */
function randomNumGenerator(factsLength) {
  let randomIndex = Math.floor(Math.random() * factsLength);
  return (randomIndex === lastRandomIndex)? 
      randomNumGenerator(factsLength):randomIndex;
}

/**
 * Implements Sticky Nav Bar
 */
document.addEventListener('DOMContentLoaded', function() {
  let navbar = document.getElementById("nav");
  let offsetPos = navbar.offsetTop;
  window.onscroll = function() {
    if (window.pageYOffset >= offsetPos) {
      navbar.classList.add("sticky")
    } else {
      navbar.classList.remove("sticky");
    }
  };
});

/**
 *Functions to toggle suggestion box form on and off
 */
function openForm() {
  document.getElementById("recommendations").style.display = "block";
  document.getElementById("datastore-form").style.display = "block";
}

function closeForm() {
  document.getElementById("recommendations").style.display = "none";
}

/**
 *Send recommendation to my Google Sheet
 */
document.addEventListener('DOMContentLoaded', (event) => {
  const scriptURL = "https://script.google.com/macros/s/AKfycbygq04RYi-5qwb82bmfONkahtZAsrz0WkSoGfHLgHVkPWnnmSI/exec";
  const form = document.forms['recommendation-form'];
							
  form.addEventListener('submit', e => {
    e.preventDefault();
    fetch(scriptURL, { method: 'POST', body: new FormData(form), mode: "no-cors"})
	  .then(response => respond("Thank you for sending :)"))
	  .catch(error => respond("Uh oh, error: " + error.message))
    
  });//the event occurred

  /**
 *Response to recommendation form
 */
  function respond(responseText) {
    document.getElementById('form-response').innerHTML += responseText;
    document.getElementById('form-response').style.visibility = "visible";
    setTimeout(() => {
        document.getElementById('form-response').innerHTML = "";
        document.getElementById('form-response').style.visibility = "hidden";
    }, 5000);
    document.getElementById('recommendation-excel').value = "";
    document.getElementById('comment-excel').value = "";
    document.getElementById('category-excel').options[0].selected = 'true';
  }
})
