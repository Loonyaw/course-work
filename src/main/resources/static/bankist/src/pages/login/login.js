"use strict";

// Select input elements and warning text
const inputLoginUsername = document.querySelector(".login__input--user");
const inputLoginPin = document.querySelector(".login__input--password");
const warningText = document.querySelector(".warning-text");

// Event listener for the login form submission
document.querySelector(".login").addEventListener("submit", function (event) {
  event.preventDefault();

  // Get input values
  const username = inputLoginUsername.value;
  const password = inputLoginPin.value;

  // Check if inputs are valid
  if (!checkInputs(username, password)) {
    return;
  }

  // Validate credentials
  validateCredentials(username, password)
    .then((isValid) => {
      if (!isValid) {
        displayWarning("Неверное имя пользователя или пароль!");
        return;
      }

      // Prompt for PIN
      const pin = prompt("Введите ваш PIN:");
      if (!pin) {
        displayWarning("PIN не введен!");
        return;
      }

      // Check login with username, password, and PIN
      checkLogin(username, password, pin);
    })
    .catch((error) => {
      console.error("Error during validation:", error);
      alert("Ошибка при проверке учетных данных. Попробуйте ещё раз.");
    });
});

// Function to validate credentials via API
function validateCredentials(username, password) {
  return fetch("http://localhost:8080/api/users/validateCredentials", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ username, password }),
  })
    .then((response) => response.json())
    .then((isValid) => {
      return isValid;
    });
}

// Function to check login via API
function checkLogin(username, password, pin) {
  fetch("http://localhost:8080/api/users/login", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ username, password, pin }),
  })
    .then((response) => {
      if (response.ok) {
        response.json().then((data) => {
          const userId = data.userId;
          sessionStorage.setItem("currentUserId", userId);
          console.log("Logged in successfully!");
          window.location.href = "/src/pages/dashboard/dashboard.html";
        });
      } else {
        displayWarning("Неверное имя пользователя, пароль или PIN!");
      }
    })
    .catch((error) => {
      console.error("Error:", error);
      alert("Ошибка при входе. Попробуйте ещё раз.");
    });
}

// Function to check if inputs are valid
function checkInputs(username, password) {
  let isValid = true;
  if (!username && !password) {
    inputLoginUsername.classList.add("warning");
    inputLoginPin.classList.add("warning");
    displayWarning("Пустые имя пользователя и пароль!");
    isValid = false;
  } else if (!username) {
    inputLoginUsername.classList.add("warning");
    displayWarning("Пустое имя пользователя!");
    isValid = false;
  } else if (!password) {
    inputLoginPin.classList.add("warning");
    displayWarning("Пустой пароль!");
    isValid = false;
  }
  return isValid;
}

// Function to display warning messages
function displayWarning(message) {
  warningText.textContent = message;
  warningText.classList.add("active");
}

// Remove warning class when user inputs data
inputLoginUsername.addEventListener("input", () => {
  inputLoginUsername.classList.remove("warning");
});

inputLoginPin.addEventListener("input", () => {
  inputLoginPin.classList.remove("warning");
});
