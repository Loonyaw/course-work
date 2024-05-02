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
const inputLoanAmount = document.querySelector(".form__input--loan-amount");
const btnLogout = document.querySelector(".btn.logout");
const labelTimer = document.querySelector(".timer");

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
      // Assuming the backend response includes card details and transactions
      const { username, transactions, card } = data;

      // Display username in the welcome message
      labelWelcome.textContent = `Добро пожаловать, ${username}!`;

      // Set the card number and CVV
      document.getElementById("cardNumber").textContent = card.cardNumber;
      document.getElementById("CVV").textContent = card.cvv;

      // Display the transactions and balance
      displayMovements(transactions);
      updateBalance(transactions);

      // Display the current date
      const now = new Date();
      labelDate.textContent = new Intl.DateTimeFormat(
        navigator.language
      ).format(now);
    })
    .catch((err) => console.error("Failed to fetch user data:", err));
}

// Event handler for money transfer
btnTransfer.addEventListener("click", function (e) {
  e.preventDefault();
  const amount = +inputTransferAmount.value;
  const receiverId = inputTransferTo.value;

  if (!receiverId || amount <= 0) {
    alert("Введите правильные данные для перевода.");
    return;
  }

  transferMoney(currentUser.id, receiverId, amount);
  inputTransferAmount.value = inputTransferTo.value = "";
});

function transferMoney(fromId, toId, amount) {
  const payload = { fromId, toId, amount };
  fetch("http://localhost:8080/api/users/transfer", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload),
  })
    .then((response) => {
      if (response.ok) return response.json();
      throw new Error("Перевод не удался");
    })
    .then((data) => {
      alert("Перевод прошел успешно");
      fetchUserData(fromId); // Refresh data
    })
    .catch((error) => alert(error.message));
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
      if (response.ok) return response.json();
      throw new Error("Ошибка при запросе займа.");
    })
    .then((data) => {
      alert("Займ одобрен");
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
    fetchUserData(userId);
    startLogoutTimer();
  } else {
    window.location.href = "/src/pages/login/login.html";
  }
});
