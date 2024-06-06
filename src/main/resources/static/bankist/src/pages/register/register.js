document.addEventListener("DOMContentLoaded", function () {
  // Elements and Variables
  const form = document.getElementById("registrationForm");
  const warningText = document.getElementById("registerWarningText");
  const currencyModal = document.getElementById("currencyModal");
  const currencySelect = document.getElementById("currencySelect");
  const currencySelectBtn = document.getElementById("currencySelectBtn");
  const currencyCancelBtn = document.getElementById("currencyCancelBtn");

  let username, email, password, pin;

  // Register the form submission event
  form.addEventListener("submit", async function (event) {
    event.preventDefault();
    let errorMessage = "";

    // Retrieve form values
    username = form.querySelector(".login__input--user").value;
    email = form.querySelector(".login__input--email").value;
    password = form.querySelector(".login__input--password").value;
    const passwordConfirm = form.querySelectorAll(".login__input--password")[1]
      .value;

    // Form validation
    if (username.length < 4) {
      errorMessage = "Имя пользователя должно быть длиннее 4 символов!";
    } else if (!validateEmail(email)) {
      errorMessage = "Введите корректный email адрес!";
    } else if (!validatePassword(password)) {
      errorMessage =
        "Пароль должен быть не менее 8 символов, содержать хотя бы одну цифру, одну заглавную и одну строчную букву.";
    } else if (password !== passwordConfirm) {
      errorMessage = "Пароли не совпадают!";
    }

    // Display validation error message if any
    if (errorMessage) {
      warningText.textContent = errorMessage;
      warningText.classList.add("active");
      return;
    }

    // Check if user already exists
    const userExists = await checkUserExists(username, email);
    if (userExists) {
      warningText.textContent =
        "Пользователь с таким username или email уже существует.";
      warningText.classList.add("active");
      return;
    }

    // Clear any previous warning messages
    warningText.textContent = "";
    warningText.classList.remove("active");

    // Show currency selection modal
    currencyModal.style.display = "block";
  });

  // Handle the currency selection
  currencySelectBtn.addEventListener("click", function () {
    const currency = currencySelect.value;
    currencyModal.style.display = "none";
    registerUserWithCurrency(username, email, password, currency);
  });

  // Handle canceling currency selection
  currencyCancelBtn.addEventListener("click", function () {
    currencyModal.style.display = "none";
  });

  // Function to check if a user exists by username or email
  async function checkUserExists(username, email) {
    const response = await fetch(
      `http://localhost:8080/api/users/exists?username=${encodeURIComponent(
        username
      )}&email=${encodeURIComponent(email)}`,
      {
        method: "GET",
        headers: { "Content-Type": "application/json" },
      }
    );
    return response.json();
  }

  // Function to register a user with a currency
  function registerUserWithCurrency(username, email, password, currency) {
    do {
      pin = prompt(
        "Введите ваш PIN для завершения регистрации (четыре цифры):"
      );
      if (!pin) {
        alert("Регистрация отменена. PIN не был введен.");
        currencyModal.style.display = "none"; // Close the currency modal
        return;
      }
    } while (!validatePin(pin));

    const userData = { username, email, password, pin, currency };

    fetch("http://localhost:8080/api/users", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(userData),
    })
      .then((response) => {
        if (response.ok) {
          alert("Регистрация прошла успешно!");
          window.location.href = "/bankist/src/pages/login/login.html";
        } else {
          response.json().then((data) => {
            alert(`Ошибка регистрации: ${data.message}`);
          });
        }
      })
      .catch((error) => {
        console.error("Ошибка при отправке данных:", error);
        alert("Ошибка при регистрации. Попробуйте ещё раз.");
      });
  }

  // Validation functions
  function validateEmail(email) {
    const re = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;
    return re.test(String(email).toLowerCase());
  }

  function validatePassword(password) {
    const re = /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,}$/;
    return re.test(String(password));
  }

  function validatePin(pin) {
    const re = /^\d{4}$/;
    return re.test(pin);
  }
});
