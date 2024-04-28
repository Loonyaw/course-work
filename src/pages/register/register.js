document.addEventListener("DOMContentLoaded", function () {
  const form = document.getElementById("registrationForm");
  const warningText = document.getElementById("registerWarningText");

  form.addEventListener("submit", async function (event) {
    event.preventDefault();
    let errorMessage = "";

    const username = form.querySelector(".login__input--user").value;
    const email = form.querySelector(".login__input--email").value;
    const password = form.querySelector(".login__input--password").value;
    const passwordConfirm = form.querySelectorAll(".login__input--password")[1]
      .value;

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

    if (errorMessage) {
      warningText.textContent = errorMessage;
      warningText.classList.add("active");
      return;
    }

    const userExists = await checkUserExists(username, email);
    if (userExists) {
      warningText.textContent =
        "Пользователь с таким username или email уже существует.";
      warningText.classList.add("active");
      return;
    }

    warningText.textContent = "";
    warningText.classList.remove("active");

    registerUser(username, email, password);
  });
});

function checkUserExists(username, email) {
  return fetch(
    `http://localhost:8080/api/users/exists?username=${encodeURIComponent(
      username
    )}&email=${encodeURIComponent(email)}`,
    {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
    }
  ).then((response) => response.json());
}

function registerUser(username, email, password) {
  const userData = {
    username: username,
    email: email,
    password: password,
  };

  fetch("http://localhost:8080/api/users", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(userData),
  })
    .then((response) => {
      if (response.ok) {
        alert("Регистрация прошла успешно!");
        window.location.href = "/src/pages/login/login.html";
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
function validateEmail(email) {
  const re = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;
  return re.test(String(email).toLowerCase());
}

function validatePassword(password) {
  const re = /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,}$/;
  return re.test(String(password));
}
