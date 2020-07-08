window.addEventListener("DOMContentLoaded", function() {

    // get the form elements defined in your form HTML above
    
    var form = document.getElementById("contact-form");
    var button = document.getElementById("button");
    var status = document.getElementById("my-form-status");

    // Success and Error functions for after the form is submitted
    
    function success() {
      form.reset();
      status.innerHTML = "Thanks! Message sent.";

    }

    function error() {
      status.innerHTML = "Oops! There was a problem.";
    }

    // handle the form submission event

    form.addEventListener("submit", function(e) {
    	e.preventDefault();
      	var data = new FormData(form);
      	ajax(form.method, form.action, data, success, error);
    });
  });
  
  // helper function for sending an AJAX request

  function ajax(method, url, data, success, error) {
    var xhr = new XMLHttpRequest();
    xhr.open(method, url);
    xhr.setRequestHeader("Accept", "application/json");
    xhr.onreadystatechange = function() {
      if (xhr.readyState !== XMLHttpRequest.DONE) return;
      if (xhr.status === 200) {
        success(xhr.response, xhr.responseType);
      } else {
        error(xhr.status, xhr.response, xhr.responseType);
      }
    };
    xhr.send(data);
 }

 function getComments() {
   //fetch('/data').then(response => {
    //console.log(response);
    //var comments = response.json();
  fetch('/data').then(response => response.json()).then((comments) => {
    // clear out the existing comments
    const currentCommentSection = document.getElementById('comments-placeholder');
    while( currentCommentSection.firstChild ){
        currentCommentSection.removeChild( currentCommentSection.firstChild );
    }
    // Build the list of comments.
    var numOfComments = 0;
    const commentsEl = document.getElementById('comments-placeholder');
    comments.forEach((comment) => {
        numOfComments ++;
      commentsEl.appendChild(createCommentElement(comment));
    });
    const numberOfCommentSection = document.getElementById('comments-num');
    numberOfCommentSection.value = numOfComments;
  });
}

 function deleteComments() {
  fetch('/delete-data').then(getComments());
}

/** Creates an <li> element containing a comment. */
function createCommentElement(comment) {
  const commentElement = document.createElement('li');
  commentElement.className = 'comment';

  const timeElement = document.createElement('span');
  timeElement.innerText = comment.convertedTime + "\t";
  const nameElement = document.createElement('span');
  nameElement.innerText = comment.name + ": \t";
  const commentTextElement = document.createElement('span');
  commentTextElement.innerText = comment.comment + "\t";
  const commentSentimentElement = document.createElement('span');
  commentSentimentElement.innerText = comment.sentimentScore ;

  commentElement.appendChild(timeElement);
  commentElement.appendChild(nameElement);
  commentElement.appendChild(commentTextElement);
  commentElement.appendChild(commentSentimentElement);

  return commentElement;
}