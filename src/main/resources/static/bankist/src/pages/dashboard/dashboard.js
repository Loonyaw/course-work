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
const btnLoanSummary = document.getElementById("btnLoanSummary");
const loanModal = document.getElementById("loanModal");
const loanDetails = document.getElementById("loanDetails");
const closeBtn = document.querySelector(".modal .close");
const repayLoanBtn = document.getElementById("repayLoan");

// Current user and currency details
let currentUser;
let currentCurrency;
const currencySymbols = {
  USD: "$",
  EUR: "€",
  GBP: "£",
  CNY: "¥",
  JPY: "¥",
  UAH: "₴",
};

// Update the display functions to accept currency
function displayMovements(movements, currencyCode) {
  containerMovements.innerHTML = "";
  const currencySymbol = currencySymbols[currencyCode] || currencyCode;

  // Sort movements by date (ascending)
  const sortedMovements = movements.sort(
    (a, b) => new Date(a.transactionDate) - new Date(b.transactionDate)
  );

  // Reverse the sorted movements to get the newest first
  const reversedMovements = sortedMovements.reverse();

  // Display each movement
  reversedMovements.forEach((mov, i) => {
    const type =
      mov.type === "LOAN_ISSUE" || mov.transactionType === "LOAN_ISSUE"
        ? "loan"
        : mov.amount > 0
        ? "deposit"
        : "withdrawal";
    const formattedDate = new Intl.DateTimeFormat(navigator.language).format(
      new Date(mov.transactionDate)
    );
    const loanDetails =
      mov.type === "LOAN_ISSUE" || mov.transactionType === "LOAN_ISSUE"
        ? `<div class="movements__loan-details">Interest Rate: ${mov.loanInterestRate}%</div>`
        : "";
    const html = `
      <div class="movements__row">
        <div class="movements__type movements__type--${type}">${
      sortedMovements.length - i
    } ${type}</div>
        <div class="movements__date">${formattedDate}</div>
        <div class="movements__value">${mov.amount.toFixed(
          2
        )} ${currencySymbol}</div>
        ${loanDetails}
      </div>
    `;
    containerMovements.insertAdjacentHTML("beforeend", html);
  });
}

// Update balance display
function updateBalance(movements, currencyCode) {
  const balance = movements.reduce((acc, mov) => acc + mov.amount, 0);
  const currencySymbol = currencySymbols[currencyCode] || currencyCode;
  labelBalance.textContent = `${balance.toFixed(2)} ${currencySymbol}`;
}

// Fetch and display user data
function fetchUserData(userId) {
  fetch(`http://localhost:8080/api/users/${userId}`)
    .then((res) => res.json())
    .then((data) => {
      // Update the currentUser object with user data
      currentUser = {
        id: data.id,
        username: data.username,
        pin: data.pin,
      };

      // Set card information
      const card = data.cards.length > 0 ? data.cards[0] : null;
      if (card) {
        const cardNumberElem = document.getElementById("cardNumber");
        const cvvElem = document.getElementById("CVV");
        currentCurrency = card.currency; // Set current currency from the card's currency

        cardNumberElem.setAttribute("data-real-value", card.cardNumber);
        cvvElem.setAttribute("data-real-value", card.cvv);

        // Initially mask the card number and CVV
        cardNumberElem.textContent = "********";
        cvvElem.textContent = "********";

        // Update balance display using card balance
        labelBalance.textContent = `${card.balance.toFixed(2)} ${
          currencySymbols[currentCurrency]
        }`;
      }

      // Combine all transactions including loan transactions
      const allMovements = [...data.transactions];
      data.loans.forEach((loan) => {
        loan.transactions.forEach((loanTransaction) => {
          allMovements.push({
            ...loanTransaction,
            type: loanTransaction.type,
            loanInterestRate: loan.interestRate,
          });
        });
      });

      // Display username in the welcome message
      labelWelcome.textContent = `Добро пожаловать, ${currentUser.username}!`;

      // Display the transactions and balance with the right currency
      displayMovements(allMovements, currentCurrency);
      updateSummary(allMovements, data.loans, currentCurrency);

      // Display the current date
      const now = new Date();
      labelDate.textContent = new Intl.DateTimeFormat(
        navigator.language
      ).format(now);

      // Display loan details in the modal
      displayLoanDetails(data.loans);
    })
    .catch((err) => {
      console.error("Не удалось получить данные пользователя:", err);
      alert("Ошибка при получении данных пользователя.");
    });
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

// Function to transfer money
function transferMoney(fromId, cardNumber, amount) {
  fetch(`http://localhost:8080/api/cards/${encodeURIComponent(cardNumber)}`)
    .then((response) => {
      if (response.ok) return response.json();
      throw new Error("Получатель с этим номером карты не найден.");
    })
    .then((card) => {
      if (card.userId) {
        const toId = card.userId;
        const payload = { fromId, toId, amount };

        return fetch("http://localhost:8080/api/users/transfer", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(payload),
        });
      } else {
        throw new Error("Пользователь с этим номером карты не найден.");
      }
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

// Function to sort movements
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

// Function to update the summary
function updateSummary(movements, loans, currencyCode) {
  const incomes = movements
    .filter((mov) => mov.amount > 0)
    .reduce((acc, mov) => acc + mov.amount, 0);

  const outgoings = movements
    .filter((mov) => mov.amount < 0)
    .reduce((acc, mov) => acc + mov.amount, 0);

  const interests = loans.reduce((acc, loan) => {
    const loanInterest = loan.amount * (loan.interestRate / 100);
    return acc + loanInterest;
  }, 0);

  const currencySymbol = currencySymbols[currencyCode] || currencyCode;
  document.getElementById("summaryIn").textContent = `${incomes.toFixed(
    2
  )} ${currencySymbol}`;
  document.getElementById("summaryOut").textContent = `${Math.abs(
    outgoings
  ).toFixed(2)} ${currencySymbol}`;
  document.getElementById("summaryInterest").textContent = `${interests.toFixed(
    2
  )} ${currencySymbol}`;
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

// Function to request a loan
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
    .catch((err) => {
      alert(err.message); // Display detailed error message
    });
}

// Event handler for loan summary
btnLoanSummary.addEventListener("click", function () {
  fetch(`http://localhost:8080/api/users/${currentUser.id}`)
    .then((res) => res.json())
    .then((data) => {
      if (data.loans.length > 0) {
        displayLoanDetails(data.loans);
        loanModal.classList.remove("hidden");
      } else {
        alert("У вас нет активных займов.");
      }
    })
    .catch((err) => console.error("Error fetching user data:", err));
});

// Close loan modal
closeBtn.addEventListener("click", function () {
  loanModal.classList.add("hidden");
});

// Prevent closing the modal by clicking outside of it
loanModal.addEventListener("click", function (e) {
  if (e.target === loanModal) {
    loanModal.classList.add("hidden");
  }
});

// Function to display loan details
function displayLoanDetails(loans) {
  const loanDetails = document.getElementById("loanDetails");
  loanDetails.innerHTML = "";
  loans.forEach((loan) => {
    const loanDetailHtml = `
      <div class="loan-detail">
        <p>Сумма: ${loan.amount.toFixed(2)} ${
      currencySymbols[currentCurrency]
    }</p>
        <p>Процентная ставка: ${loan.interestRate}%</p>
        <p>Дата выдачи: ${new Intl.DateTimeFormat(navigator.language).format(
          new Date(loan.issueDate)
        )}</p>
        <p>Дата погашения: ${new Intl.DateTimeFormat(navigator.language).format(
          new Date(loan.dueDate)
        )}</p>
        <button class="form__btn repay__btn" data-loan-id="${
          loan.id
        }" data-loan-amount="${loan.amount}">
          Выплатить займ &rarr;
        </button>
        <br>
      </div>
    `;
    loanDetails.insertAdjacentHTML("beforeend", loanDetailHtml);
  });

  // Add event listeners for repay buttons
  const repayLoanButtons = document.querySelectorAll(".repay__btn");
  repayLoanButtons.forEach((button) => {
    button.addEventListener("click", function () {
      const loanId = this.getAttribute("data-loan-id");
      const loanAmount = parseFloat(this.getAttribute("data-loan-amount"));
      const repaymentAmount = parseFloat(prompt("Введите сумму для оплаты:"));

      if (!isNaN(repaymentAmount) && repaymentAmount > 0) {
        if (repaymentAmount > loanAmount) {
          alert("Сумма платежа не может превышать оставшуюся сумму кредита.");
        } else {
          repayLoan(currentUser.id, loanId, repaymentAmount);
        }
      } else {
        alert("Введите корректную сумму.");
      }
    });
  });
}

// Function to repay a loan
function repayLoan(userId, loanId, repaymentAmount) {
  fetch(`http://localhost:8080/api/loans/${loanId}/repay`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ amount: repaymentAmount }),
  })
    .then((response) => {
      if (!response.ok) {
        return response.text().then((text) => {
          throw new Error(text);
        });
      }
      return response.text();
    })
    .then((message) => {
      alert(message);
      fetchUserData(userId); // Refresh user data
      loanModal.classList.add("hidden");
    })
    .catch((error) => {
      alert(error.message);
    });
}

// Logout logic
btnLogout.addEventListener("click", function () {
  // Clear session data
  console.log("Пользователь вышел из системы");
  sessionStorage.clear();
  window.location.href = "/src/pages/login/login.html";
});

// Event handler for sorting movements
btnSort.addEventListener("click", function () {
  sortMovements();
});

// Event handler for closing account
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
