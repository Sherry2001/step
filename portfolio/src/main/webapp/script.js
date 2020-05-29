let lastRandomIndex;

/**
 * Adds a random fact about myself!
 */
function genRandomFact() {
  const facts = [
    'I love spicy food, but I dislike eating peppers!',
    'I play the flute!',
    'I have been taking lots of walks during quarantine',
    'I am 19 years old!',
  ];

  let randomIndex = randomNumGenerator(facts.length);
  console.log(randomIndex);
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
  return (randomIndex===lastRandomIndex)? randomNumGenerator(factsLength):randomIndex;
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
  document.getElementById("recommendations").style.display="block";
}

function closeForm() {
  document.getElementById("recommendations").style.display="none";
}