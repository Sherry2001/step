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
  fetch('/data?max=' + maxLoad).then(response => response.json()).then(toDos => {
    const literatureList = document.getElementById('literature-list');
    const musicList = document.getElementById('music-list');
    const movieList = document.getElementById('movie-list');
    const travelList = document.getElementById('travel-list');
    
    literatureList.innerHTML = '';
    musicList.innerHTML = '';
    movieList.innerHTML = '';
    travelList.innerHTML = '';

    for (let toDo of toDos) {
      const recommendation = createRecommendationElement(toDo);
      const category = toDo.category;

      if (category == 'Literature') {
        literatureList.appendChild(recommendation);
      } else if (category == 'Music') {
        musicList.appendChild(recommendation);
      } else if (category == 'Movie' || category == 'TV-Show') {
        movieList.appendChild(recommendation);
      } else {
        travelList.appendChild(recommendation); 
      }
    } 
  });
}

/**
 * Helper to create a recommendation
 * Creates a <div class="recommendation"> element
 */
function createRecommendationElement(toDo) {
  const recommendation = document.createElement('div');
  recommendation.className = 'recommendation';

  const content =  document.createElement('div');
  content.className = 'content';
  content.innerHTML = toDo.content;
  recommendation.appendChild(content);

  const comment = document.createElement('div');
  comment.className = 'comment';
  comment.innerHTML = toDo.comment; 
  recommendation.appendChild(comment);

  if (toDo.imageUrl !== undefined){
    const image = new Image();
    image.src = toDo.imageUrl;
    recommendation.appendChild(image);
  }
  
  const name = document.createElement('div');
  name.className = 'footer';
  name.innerHTML = toDo.category +' rec by: ' + toDo.name;
  recommendation.appendChild(name);

  return recommendation;
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
function generateRandomFact() {
  let randomIndex = randomNumberGenerator(facts.length);
  lastRandomIndex = randomIndex;
  let fact = facts[randomIndex];

  const factContainer = document.getElementById('fact-container');
  factContainer.innerText = fact;
}

/**
 * Generates random number from 0 to parameter factsLength, no repeat
 * @param factsLength
 */
function randomNumberGenerator(factsLength) {
  let randomIndex = Math.floor(Math.random() * factsLength);
  return (randomIndex === lastRandomIndex)? 
      randomNumberGenerator(factsLength):randomIndex;
}

/**
 * Implements Sticky Nav Bar
 */
document.addEventListener('DOMContentLoaded', function() {
  let navbar = document.getElementById('nav');
  let offsetPos = navbar.offsetTop;
  window.onscroll = function() {
    if (window.pageYOffset >= offsetPos) {
      navbar.classList.add('sticky')
    } else {
      navbar.classList.remove('sticky');
    }
  };
});

/**
 *Functions to toggle suggestion box form on and off
 */
function toggleForm() {
  const form = document.getElementById('recommendation-form');
  const imageForm = document.getElementById('datastore-form');
  if (form.style.display == 'block') {
    form.style.display = 'none';
    imageForm.style.display = 'none';
  } else {
    form.style.display = 'block';
    imageForm.style.display = 'block';
    setFormActionBlobstoreUrl('datastore-form'); 
  }
}

/**
 * Sets form action to blobstore upload url 
 */
function setFormActionBlobstoreUrl(formId) {
  fetch('/blobstore-url')
      .then((response) => {
        return response.text();
      })
      .then((blobstoreUrl) => {
        let imageForm = document.getElementById(formId);
        imageForm.action = blobstoreUrl;
      });
}


/**
 *Listens to page load
 */
document.addEventListener('DOMContentLoaded', (event) => {
  const scriptURL = 'https://script.google.com/macros/s/AKfycbygq04RYi-5qwb82bmfONkahtZAsrz0WkSoGfHLgHVkPWnnmSI/exec';
  const form = document.forms['recommendation-form'];

  /**
   * Upon submission, adds data to both datastore and google sheets
   */
  form.addEventListener('submit', e => {
    e.preventDefault();
    const name = document.getElementById('name-excel').value;
    const category = document.getElementById('category-excel').value;
    const recommendation = document.getElementById('content-excel').value;
    const comment = document.getElementById('comment-excel').value;	
    const url = '/data?name=' + name + '&category=' + category + '&content=' + recommendation +
               '&comment=' + comment;


    fetch(scriptURL, { method: 'POST', body: new FormData(form), mode: 'no-cors'})
    .then(() => {
      fetch (url, {method: 'POST'});
    })
    .then(() => {
      getData();
      respond("Thank you for your recommendation :)");
    })
    .catch(error => respond('Uh oh, error: ' + error.message))
  })
});

/**
 *Response to recommendation form
 */
function respond(responseText) {
  document.getElementById('form-response').innerHTML += responseText;
  document.getElementById('form-response').style.visibility = 'visible';
  setTimeout(() => {
      document.getElementById('form-response').innerHTML = '';
      document.getElementById('form-response').style.visibility = 'hidden';
  }, 5000);
  document.getElementById('content-excel').value = '';
  document.getElementById('comment-excel').value = '';
  document.getElementById('category-excel').options[0].selected = 'true';
}

