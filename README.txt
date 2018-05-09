Group 0214
-----------------
Donghee Kim
Gihyun Kim
Yujie Miao
Ang Li (Bill-linux, Bill)


TODO
- Server/Client
    Allows for multiple clients to be run across different computers
- GUI
    login screen:
        enter the id of the employee and screen would change in to the according employee screen
        if computer server is not connected or the employee id is invalid, it would not log in
    server screen:
        employee can select table that displayed on the screen
        it has buttond for many actions:
            receive button: receive ingredients, and update the inventory
            log out: log out
            take seat: enter the amount of customer and select the table they are sitting
            clear: clear the selected table
            order: the selected table is making an order, and screen change into menu screen
            print bill: print bill for the selected table, and change screen into print bill screen
            delivered fail: customer doesn't want the select dish
            delivered success: the selected dish is delivered
    menu Screen:
        it has buttons for many actions:
            order: the customer ordered the select dish from menu column, and pop up window for adjustment
            delete: delete the selected dish from order column
            submit: submit the order and go back server screen
    Ingredient screen:
        show all the ingredient and buttons for adjustment, click done when customer finished the adjustment
    printbill screen:
        all dish for this table is shown on the left right.
        select the dishes and confirm. and then click pay

    manager screen:
        it has buttons for many actions:
        check inventory: show all the ingredient in inventory
        request for more: show all item that is going low
        dish in progress: show all dishes that not delivered

    cook screen:
        show the dish that are in progress and waiting in line
        select a dish and click finish to finish a dish.
        when all dish in progress is finished, cook can click confirm next order to put the next order into progress

- Logger
    - RestaurantLogger
        - Logs configuration information in CONFIG level
        - Logs any errors that occur in WARNING level
        - Logs any inputs that server, cook, manager make in INFO.level
    - BillLogger
        - Keeps track of all paid bills.


menu.txt
===========================================
- this file is used to initialize the menu
- please enter dish in this format:
dishName;cost;firstIngredient:defaultAmount:minAmount:maxAmount,second Ingredient...
- no space in between, except dishName and ingredientName.
- dishName, cost, and ingredient are separated by semicolon
- ingredient, default, allowed subtraction, and allowed addition are separated by colon
- ingredients are separated by comma
- this file will be recreated as an empty file by the program when deleted


starter.txt
===========================================
- this file is used to initialize the program with the number of employees, backend.table, and number of ingredients
- please only enter one integer in the first 4 lines:
    1st number represents the amount of Tables
    2nd number represents the number of Servers
    3rd number represents the number of Cooks
    4th number represents the number of Managers
- each line after fourth line are backend.inventory stock, represented in this format:
    ingredientName,currentAmount,lowerThreshold
- no space after the comma
- all amount are integers
- when the ingredients are below the lowerThreshold, the program will write to request.txt requesting for more backend.inventory
- this file will be recreated as an empty file by the program when deleted


request.txt
===========================================
- it contains all items that the Manager going to order from
- it will be in the following format with a space in between name and amount:
    ingredientName1  amount1
    ingredientName2  amount2
          ...          ...
    ingredientNameN  amountN

- after the request is sent, the manager should manually clear the request.txt
- we are assuming no ingredient is below threshold when the program starts, so if an ingredient is below threshold when the program starts, request.txt won’t generate a new line

Corner Cases
===========================================
1. runningQuantity is used to determine if a dish is available for order. Since the actual quantity of ingredients in the restaurant are not deducted until after a dish is prepared by the cook and
ready for delivery, we need a separate quantity variable to update the quantity every a dish is added to the order. Thus, the runningQuantity is 'updated' faster than the actual quantity for the
inventory ingredient would be, preventing creation of orders for which there are not enough ingredients left in the inventory.

2. Our program allows a seated table to be cleared even after ordering dishes. This flexibility allows the program to accommodate outlying situations such as where a customer may have ordered dishes
and decided to leave without notifying any of the restaurant employees due to overwhelming delay, or other unforeseen circumstances. We leave it up to the user of the program to ensure that the bill
is printed before clearing the table, since so far there is no way to recall the information of the cleared table and thus it would be impossible to print bills for a table that has been cleared.

3.


1. computerServer delivers dish before the dish is made - This exception will not occur in phase 2 because we will make the Server select the delivered dish from the frontend.GUI, which only gives valid options (i.e. cooked dishes) so that the program will not have NullPointerException
2. invalid employeeID - Warns the user of this error in input and does not call the method to run. This should not exist in phase 2 as input will not be in the form of backend.event.txt, but specified input according to the frontend.GUI
3. note: Our plan for phase 2 is to have the Manager enter the menu and ingredients with frontend.GUI, so all the exceptions caused by parsing the the input from backend.event.txt should not happen.
4. we will implement save data (e.g. backend.inventory) to offline text files in phase 2, since by then the user will be able to close the program properly

UML Format
===========================================
hollow arrow - inheritance
solid arrow - association
diamond arrow - composition
underline - static
italics - abstract method
hollow shape - variable/constant
filled shape - method
red square - private
blue triangle - package-private
green circle - public