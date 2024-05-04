"use strict";

// DOM Elements
const labelWelcome = document.querySelector(".welcome");
const labelBalance = document.querySelector(".balance__value");
const containerMovements = document.querySelector(".movements");
const labelDate = document.querySelector(".date");
const btnTransfer = document.querySelector(".form__btn--transfer");
const inputTransferTo = document.querySelector(".form__input--to");
const inputTransferAmount = document.querySelector(".form__input--amount");
const btnLoan = document.querySelector(".form__btn--loan");
const btnClose = document.querySelector(".form__btn--close");
const inputLoanAmount = document.querySelector(".form__input--loan-amount");
const btnLogout = document.querySelector(".btn.logout");
const labelTimer = document.querySelector(".timer");
const inputClosePin = document.querySelector(".form__input--pin");
const inputCloseUser = document.querySelector(".form__input--user");
const btnSort = document.getElementById("btnSort");

// Current user
let currentUser;

// Display movements
function displayMovements(movements) {
  containerMovements.innerHTML = "";
  movements.forEach((mov, i) => {
    const type = mov.amount > 0 ? "deposit" : "withdrawal";
    const formattedDate = new Intl.DateTimeFormat(navigator.language).format(
      new Date(mov.transactionDate)
    );
    const html = `
      <div class="movements__row">
          <div class="movements__type movements__type--${type}">${
      i + 1
    } ${type}</div>
          <div class="movements__date">${formattedDate}</div>
          <div class="movements__value">${mov.amount.toFixed(2)}€</div>
      </div>
    `;
    containerMovements.insertAdjacentHTML("afterbegin", html);
  });
}

// Display balance
function updateBalance(movements) {
  const balance = movements.reduce((acc, mov) => acc + mov.amount, 0);
  labelBalance.textContent = `${balance.toFixed(2)}€`;
}

// Fetch and display user data
function fetchUserData(userId) {
  fetch(`http://localhost:8080/api/users/${userId}`)
    .then((res) => res.json())
    .then((data) => {
      // Update the `currentUser` object with user data
      currentUser = {
        id: data.id,
        username: data.username,
        pin: data.pin,
      };

      // Assuming the backend response includes card details and transactions
      const { transactions, card } = data;

      // Display username in the welcome message
      labelWelcome.textContent = `Добро пожаловать, ${currentUser.username}!`;

      // Set the card number and CVV with attributes for real values
      const cardNumberElem = document.getElementById("cardNumber");
      const cvvElem = document.getElementById("CVV");

      cardNumberElem.setAttribute("data-real-value", card.cardNumber);
      cvvElem.setAttribute("data-real-value", card.cvv);

      // Initially mask the card number and CVV
      cardNumberElem.textContent = "********";
      cvvElem.textContent = "********";

      // Display the transactions and balance
      displayMovements(transactions);
      updateBalance(transactions);
      updateSummary(transactions);

      // Display the current date
      const now = new Date();
      labelDate.textContent = new Intl.DateTimeFormat(
        navigator.language
      ).format(now);
    })
    .catch((err) =>
      console.error("Не удалось получить данные пользователя:", err)
    );
}

// Function to toggle the visibility of sensitive card information
function toggleVisibility(element) {
  const hiddenValue = "********";
  if (element.textContent === hiddenValue) {
    element.textContent = element.getAttribute("data-real-value");
  } else {
    element.textContent = hiddenValue;
  }
}

// Add event listeners to toggle card number and CVV visibility
document.getElementById("cardNumber").addEventListener("click", function () {
  toggleVisibility(this);
});

document.getElementById("CVV").addEventListener("click", function () {
  toggleVisibility(this);
});

// Event handler for money transfer
btnTransfer.addEventListener("click", function (e) {
  e.preventDefault();
  const amount = parseFloat(inputTransferAmount.value);
  const cardNumber = inputTransferTo.value.trim(); // Use the card number

  if (!cardNumber || isNaN(amount) || amount <= 0) {
    alert("Введите правильные данные для перевода.");
    return;
  }

  transferMoney(currentUser.id, cardNumber, amount);
  inputTransferAmount.value = inputTransferTo.value = "";
});

function transferMoney(fromId, cardNumber, amount) {
  // Fetch user ID by card number
  fetch(
    `http://localhost:8080/api/users/card/${encodeURIComponent(cardNumber)}`
  )
    .then((response) => {
      if (response.ok) return response.json();
      throw new Error("Получатель с этим номером карты не найден.");
    })
    .then((toId) => {
      // Proceed with transfer
      const payload = { fromId, toId, amount };
      return fetch("http://localhost:8080/api/users/transfer", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      });
    })
    .then((response) => {
      if (response.ok) return response.json();
      throw new Error("Перевод не удался");
    })
    .then(() => {
      alert("Перевод прошел успешно");
      fetchUserData(fromId); // Refresh data
    })
    .catch((error) => alert(error.message));
}

let ascendingOrder = true;
function sortMovements() {
  const movementsRows = Array.from(
    document.querySelectorAll(".movements__row")
  );

  const sortOrder = ascendingOrder ? 1 : -1;

  const sortedRows = movementsRows.sort((a, b) => {
    const amountA = parseFloat(
      a.querySelector(".movements__value").textContent
    );
    const amountB = parseFloat(
      b.querySelector(".movements__value").textContent
    );
    return sortOrder * (amountA - amountB);
  });

  ascendingOrder = !ascendingOrder;

  containerMovements.innerHTML = "";

  sortedRows.forEach((row) => {
    containerMovements.appendChild(row);
  });
}
function updateSummary(movements) {
  const incomes = movements
    .filter((mov) => mov.amount > 0)
    .reduce((acc, mov) => acc + mov.amount, 0);

  const outgoings = movements
    .filter((mov) => mov.amount < 0)
    .reduce((acc, mov) => acc + mov.amount, 0);

  // const interest = movements
  //   .filter((mov) => mov.amount > 0)
  //   .map((mov) => mov.amount * 0.1) // Assuming interest is 10% of deposits
  //   .reduce((acc, int) => acc + int, 0);

  document.getElementById("summaryIn").textContent = `${incomes.toFixed(2)}€`;
  document.getElementById("summaryOut").textContent = `${Math.abs(
    outgoings
  ).toFixed(2)}€`;
  // document.getElementById("summaryInterest").textContent = `${interest.toFixed(
  //   2
  // )}€`;
}

// Event handler for requesting a loan
btnLoan.addEventListener("click", function (e) {
  e.preventDefault();
  const amount = Math.floor(inputLoanAmount.value);
  if (amount <= 0) {
    alert("Введите сумму займа больше 0.");
    return;
  }
  requestLoan(currentUser.id, amount);
  inputLoanAmount.value = "";
});

function requestLoan(userId, amount) {
  fetch(`http://localhost:8080/api/users/${userId}/requestLoan`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ amount }),
  })
    .then((response) => {
      if (!response.ok) {
        return response.json().then((data) => {
          throw new Error(data.message || "Ошибка при запросе займа.");
        });
      }
      return response.json(); // Parse the JSON response
    })
    .then((data) => {
      alert(data.message); // Use the message from the JSON response
      fetchUserData(userId); // Refresh data
    })
    .catch((err) => alert(err.message));
}

// Logout logic
btnLogout.addEventListener("click", function () {
  // Clear session data
  console.log("Пользователь вышел из системы");
  sessionStorage.clear();
  window.location.href = "/src/pages/login/login.html";
});

btnSort.addEventListener("click", function () {
  sortMovements();
});

btnClose.addEventListener("click", function (e) {
  e.preventDefault();
  const username = inputCloseUser.value.trim();
  const pin = inputClosePin.value.trim();

  // Check the input credentials against `currentUser`
  if (username !== currentUser.username || pin !== currentUser.pin) {
    alert("Неправильное имя пользователя или PIN.");
    return;
  }

  // Proceed with the delete request
  fetch(`http://localhost:8080/api/users/${currentUser.id}`, {
    method: "DELETE",
    headers: { "Content-Type": "application/json" },
  })
    .then((response) => {
      if (!response.ok) {
        return response.json().then((data) => {
          throw new Error(data.message || "Ошибка при закрытии аккаунта.");
        });
      }
      // No response body expected on successful DELETE
      sessionStorage.clear();
      alert("Ваш аккаунт успешно закрыт.");
      window.location.href = "/src/pages/login/login.html"; // Redirect to login page
    })
    .catch((err) => alert(err.message));
});

// Implement a logout timer for security
function startLogoutTimer() {
  let time = 300; // 5 minutes in seconds
  const timer = setInterval(() => {
    const minutes = String(Math.floor(time / 60)).padStart(2, "0");
    const seconds = String(time % 60).padStart(2, "0");
    labelTimer.textContent = `${minutes}:${seconds}`;
    if (time === 0) {
      clearInterval(timer);
      sessionStorage.clear();
      window.location.href = "/src/pages/login/login.html";
    }
    time--;
  }, 1000);
  return timer;
}

// On page load
document.addEventListener("DOMContentLoaded", function () {
  const userId = sessionStorage.getItem("currentUserId");
  if (userId) {
    currentUser = { id: userId }; // Setting the current user object
    fetchUserData(userId);
    startLogoutTimer();
  } else {
    window.location.href = "/src/pages/login/login.html";
  }
});
