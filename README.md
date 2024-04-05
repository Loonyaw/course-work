# Лабораторна робота 2. Інтеграція СУБД у веб-сервіс Spring. Технологія ORM

### Задачі:

1. Створити класи сутностей відповідно до завдання. Прописати реляційні відносити між сутностями
2. Розробити один або декілька класів REST-контролерів
3. Створити методи для обробки запитів відповідно до завдання
4. Створити класи сервісів, в яких передбачити впровадження об'єктів репозиторіїв і виклик методів для реалізації запитів
5. Створити інтерфейси репозиторіїв для кожної сутності і, за потреби, оголосити потрібні методи для реалізації запитів до бази
6. Створити колекцію запитів в Postman
7. Створити запити відповідно до варіанту завдання
8. Запустити серверний додаток
9. Переконатися, що всі запити, створені у Postman, працюють коректно та повертають коректний результат
10. Експортувати колекцію запитів в формат JSON та додати до цього програмного проєкту

**Варіант завдання обирається відповідно до списку групи, який надає староста групи**

>**Всюди в завданнях, де вимагається вказати певну сутність - треба вказати id екземпляру сутності**

**Task Variant 1: Real Estate Management System**

Topic: Design a RESTful API for a real estate management system using Java Spring, integrating with a database using ORM.

Entities:

Property

- Attributes: id, address, price, size, typeId, ownerId
- Relations: Many to One with PropertyType, Many to One with Owner

PropertyType

- Attributes: id, typeName, description
- Relations: One to Many with Property

Owner

- Attributes: id, name, contactNumber, email
- Relations: One to Many with Property

Queries:

1. List all properties with their type and owner details.
2. Retrieve properties above a specific price, including their type and owner information.
3. Find all properties by a specific owner, including the property type details.
4. Get all property types and the properties listed under each type.
5. List properties within a specific size range, including their type and owner details.
6. Retrieve the top 3 most common property types, including all properties of those types.
7. Get all owners with more than a certain number of properties, including their property details.

**Task Variant 2: Vehicle Rental System**

Topic: Develop a RESTful API for a vehicle rental system using Java Spring, with ORM database integration.

Entities:

Vehicle

- Attributes: id, model, make, year, rentalPricePerDay, typeId, rentalStationId
- Relations: Many to One with VehicleType, Many to One with RentalStation

VehicleType

- Attributes: id, typeName, description
- Relations: One to Many with Vehicle

RentalStation
- Attributes: id, name, location, contactInfo
- Relations: One to Many with Vehicle

Queries:

1. List all vehicles with their type and rental station details.
2. Retrieve all vehicles of a specific type, including their rental station information.
3. Find vehicles available for rental at a specific station, including their type details.
4. Get all rental stations and the vehicles available at each station.
5. List vehicles with a rental price below a specific threshold, including type and station details.
6. Get all rental stations with more than a specific number of vehicles available, including their vehicle details.
7. List all vehicles sorted by year, including their type and rental station details.


**Task variant 3: Employee Management System**

Topic: Construct a RESTful API for an employee management system using Java Spring, integrating with ORM for database operations.

Entities:

Employee

- Attributes: id, name, position, salary, departmentId, managerId
- Relations: Many to One with Department, Many to One with Manager

Department

- Attributes: id, name, location, headId
- Relations: One to Many with Employee, One to One with Manager

Manager

- Attributes: id, employeeId
- Relations: One to One with Department, One to Many with Employee

Queries:

1. List all employees with their department and manager details.
2. Retrieve employees earning above a certain salary, including their department and manager.
3. Find all employees in a specific department, including their managers.
4. Get details of a department and its employees, including the department manager.
5. List managers and their team members, including department details.
6. Retrieve departments with more than a certain number of employees, including all employee details.
7. Retrieve all departments and their manager details, including employees in each department.

**Task variant 4: Inventory Management System**

Topic: Develop a RESTful API for an inventory management system using Java Spring, with ORM for database integration.

Entities:

Product

- Attributes: id, name, price, stockQuantity, categoryId, supplierId
- Relations: Many to One with Category, Many to One with Supplier

Category

- Attributes: id, categoryName, description
- Relations: One to Many with Product

Supplier

- Attributes: id, name, contactInfo, country
- Relations: One to Many with Product

Queries:

1. List all products with their category and supplier details.
2. Retrieve products with stock quantity below a specific level, including their category and supplier.
3. Find all products in a specific category, including their suppliers.
4. List categories with more than a certain number of products, including all products in those categories.
5. Retrieve products with a price above a specific threshold, including category and supplier.
6. Find all products supplied by a specific supplier, including their categories.
7. Get the top 3 categories with the highest total stock quantity, including their products and suppliers.

**Task variant 5: Travel Agency Management System**

Topic: Build a RESTful API for a travel agency management system using Java Spring, with database integration through ORM.

Entities:

Trip

- Attributes: id, destination, duration, price, guideId, packageId
- Relations: Many to One with Guide, Many to One with TravelPackage

Guide

- Attributes: id, name, language, experienceYears
- Relations: One to Many with Trip

TravelPackage

- Attributes: id, name, description, startDate, endDate
- Relations: One to Many with Trip

Queries:

1. List all trips with their guide and travel package details.
2. Retrieve trips to a specific destination, including their guide and package information.
3. Find all trips with a duration longer than a certain period, including guide and package details.
4. Get all guides and the trips they lead, including travel package information.
5. List travel packages available in a specific date range, including all associated trips.
6. Retrieve the top 3 most popular destinations, including all trips, guides, and packages for those destinations.
7. Find trips under a certain price, including details about the guide and travel package.

**Task variant 6: Library Management System**

Topic: Design a REST API for a library management system using Java Spring, with ORM database integration.

Entities:

Book

- Attributes: id, title, author, isbn, publicationYear, shelfId, borrowerId
- Relations: Many to One with Shelf, Many to One with Borrower

Shelf

- Attributes: id, location, capacity
- Relations: One to Many with Book

Borrower

- Attributes: id, name, membershipNumber, contactInfo
- Relations: One to Many with Book

Queries:

1. List all books with their shelf location and borrower details.
2. Retrieve books by a specific author, including their shelf and borrower information.
3. Find all books borrowed by a specific borrower, including their shelf locations.
4. Get all shelves with their books, including borrower details for each book.
5. List borrowers who have borrowed more than a certain number of books, including the details of these books.
6. Retrieve books published in a specific year range, including their shelf and borrower details.
7. Retrieve all shelves with more than a specific number of books, including all books on each shelf.


**Task variant 7: Fitness Center Management System**

Topic: Create a RESTful API for a fitness center management system using Java Spring, focusing on database integration with ORM.

Entities:

Member

- Attributes: id, name, age, membershipType, trainerId
- Relations: Many to One with Trainer, Many to One with MembershipType

Trainer

- Attributes: id, name, specialization, experienceYears
- Relations: One to Many with Member

MembershipType

- Attributes: id, typeName, duration, price
- Relations: One to Many with Member

Queries:

1. List all members with their trainer and membership type details.
2. Retrieve members who have a membership of a specific type, including their trainers.
3. Find all trainers with a specific specialization, including their members.
4. Get all membership types and the members subscribed to each type.
5. List members above a certain age, including their trainer and membership type.
6. Retrieve the top 3 most popular membership types, including all members subscribed to them.
7. Find all members trained by a specific trainer, including their membership type.

**Task variant 8: Agricultural Management System**

Topic: Develop a RESTful API for an agricultural management system using Java Spring, with ORM for database operations.

Entities:

Crop

- Attributes: id, name, type, plantingSeason, harvestSeason, farmerId
- Relations: Many to One with Farmer, Many to One with CropType

Farmer

- Attributes: id, name, experienceYears, farmSize
- Relations: One to Many with Crop

CropType

- Attributes: id, typeName, characteristics
- Relations: One to Many with Crop

Queries:

1. List all crops with their farmer and crop type details.
2. Retrieve crops of a specific type, including their farmer information.
3. Find all farmers with more than a certain size of farm, including their crops.
4. Get all crop types and the crops associated with each type.
5. List crops that are in season for planting or harvesting, including their farmer and crop type.
6. Retrieve the top 3 most common crop types, including all associated crops and farmers.
7. Find crops with a specific harvesting season, including their farmer and crop type.

**Task variant 9: Restaurant Reservation System**

Topic: Develop a RESTful API for a restaurant reservation system using Java Spring, integrating with ORM for database interactions.

Entities:

Reservation

- Attributes: id, dateTime, numberOfPeople, customerId, tableId
- Relations: Many to One with Customer, Many to One with Table

Customer

- Attributes: id, name, contactNumber, email
- Relations: One to Many with Reservation

Table

- Attributes: id, number, capacity, location
- Relations: One to Many with Reservation

Queries:

1. List all reservations with customer and table details.
2. Retrieve reservations for a specific date and time, including customer and table information.
3. Find all customers who have made more than a specific number of reservations, including their reservations.
4. List reservations for a specific number of people, including customer and table details.
5. Retrieve the top 3 most booked tables, including all reservations for those tables.
6. Find reservations made by a specific customer, including table details.
7. Retrieve all tables in a specific location of the restaurant, including their reservations.

**Task variant 10: City Transport System**

Topic: Build a RESTful API for a city transport system using Java Spring, with ORM for database interactions.

Entities:

Bus

- Attributes: id, routeNumber, capacity, scheduleId, driverId
- Relations: Many to One with Route, Many to One with Driver

Route

- Attributes: id, routeName, startPoint, endPoint
- Relations: One to Many with Bus

Driver

- Attributes: id, name, licenseNumber, yearsOfExperience
- Relations: One to Many with Bus

Queries:

1. List all buses with their routes and driver details.
2. Retrieve all buses on a specific route, including driver information.
3. Find all drivers with more than a specific number of years of experience, including the buses they drive.
4. Get all routes and the buses that operate on them.
5. List buses with a capacity exceeding a certain number, including their route and driver.
6. Retrieve the top 3 most frequent routes, including all buses operating on them.
7. Find all buses driven by a specific driver, including their routes.