
// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * Adds a random greeting to the page.
 */
function addRandomImage() {
    const pictures = ['9B9BC8F0-E900-4C97-9ED8-DE244974CE56.JPG', 'IMG-1944.jpg IMG-2293.jpg', 'IMG-0990.JPG', 'IMG-2029.jpg', 'IMG-7113.jpg', 'IMG-1926.jpg', 'IMG-2217.jpg', 'IMG-8551.jpg']
  // Pick a random picture.
    const picture = pictures[Math.floor(Math.random() * pictures.length)];

  // Add it to the page.
    const imageContainer = document.getElementById('image-container');
    // greetingContainer.innerText = picture;

    var img = imageContainer.querySelector('img')
    img.src = 'images/IMG-2293.jpg'
    //img.setAttribute('src', 'images/IMG-2293.jpg');
    
}
