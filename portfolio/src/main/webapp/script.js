/**
 * Delete from datastore
 */
function deleteData() {
  fetch('/delete-data', {method:'POST'}).then(() => {
      document.getElementById("alert-card").style.display = 'none';
      getData();
  });
}

function deleteBuffer() {
  fetch('/checklogin').then((response) => response.json())
  .then((responseJson) => {
    const alertCard = document.getElementById('alert-card');
    alertCard.innerHTML = '';
    let message = document.createElement('p');

    if (responseJson.loggedIn) {
      const email = responseJson.email; 
      if (email === 'sherryshi2001@gmail.com' || email === 'shershi@google.com' ||
          email === 'alfredh@google.com' || email === 'ricazhang@google.com') {
        message.innerHTML = 'Hi Sherry, are you sure you want to delete?';
        alertCard.appendChild(message);
        const deleteButton = document.createElement('button'); 
        deleteButton.innerHTML = 'Delete';
        deleteButton.onclick = () => {deleteData()};
        alertCard.appendChild(deleteButton);
      } else {
        message.innerHTML = 'Sorry, only Sherry can delete these';
        alertCard.appendChild(message);
      }
      const logoutLink = document.createElement('a');
      const linkText = document.createTextNode("Log Out");
      logoutLink.appendChild(linkText);
      logoutLink.href = responseJson.url;
      alertCard.appendChild(logoutLink);
    } else {
      message.innerHTML = 'Only Sherry can delete these. If you are Sherry, please log in!';
      alertCard.appendChild(message);
      const loginLink = document.createElement('a');
      const linkText = document.createTextNode('Log In!');
      loginLink.appendChild(linkText);
      loginLink.href = responseJson.url;
      alertCard.appendChild(loginLink);
    }
    showAlertMode(alertCard);
  })
}

/**
 * Helper to show alert panel and blacked out background
 */
function showAlertMode(alertCard) {
  alertCard.classList.add('active');
  const backdrop = document.getElementById('backdrop');
  backdrop.classList.add('active');
  backdrop.addEventListener('click',() => {
    backdrop.classList.remove('active');
    alertCard.classList.remove('active');
  })
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

      if (category === 'Literature') {
        literatureList.appendChild(recommendation);
      } else if (category === 'Music') {
        musicList.appendChild(recommendation);
      } else if (category === 'Movie' || category === 'TV-Show') {
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

  if (toDo.imageUrl){
    const image = new Image();
    image.src = toDo.imageUrl;
    image.alt = toDo.content;
    recommendation.appendChild(image);
  }
  
  const name = document.createElement('div');
  name.className = 'footer';
  name.innerHTML = toDo.category +' rec by: ' + toDo.name + 
    '<i class="fa fa-trash-o" onclick="deleteBuffer()"></i>';
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
 *Functions to toggle suggestion box form on and off
 */
function toggleForm() {
  const form = document.getElementById('recommendation-form');
  const imageForm = document.getElementById('datastore-form');
  if (form.style.display === 'block') {
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
 * Performs the following after page loads
 */
document.addEventListener('DOMContentLoaded', (event) => {
  /**
   * Implements Sticky Nav Bar
   */
  let navbar = document.getElementById('nav');
  let offsetPos = navbar.offsetTop;
  window.onscroll = function() {
    if (window.pageYOffset >= offsetPos) {
      navbar.classList.add('sticky')
    } else {
      navbar.classList.remove('sticky');
    }
  };

  /**
   * Form submission-  posts form data to datatstore and google sheets
   */
  const scriptURL = 'https://script.google.com/macros/s/AKfycbygq04RYi-5qwb82bmfONkahtZAsrz0WkSoGfHLgHVkPWnnmSI/exec';
  const form = document.forms['recommendation-form'];
  
  form.addEventListener('submit', (e) => {
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
      respond('Thank you for your recommendation :)');
    })
    .catch((error) => respond('Uh oh, error: ' + error.message))
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
