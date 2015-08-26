# TodoApp

Todo app is an android app that lets the user create a list of tasks and allows them to create new items, edit and delete existing items. The user can also add notes to a task item and set a date when the task is due. The data is persisted using SQLite.

Submitted by: Sushma Nayak

#Submission Checklist

Required Functionality:

*	Can successfully add and remove items from the todo list 
*	There is support for editing todo items
*	The app persists todo items into SQLite and retrieves them properly on app restart

Optional Functionality:

* Persist the todo items into SQLite instead of a text file
*	Used a recyclerview to improve style of the todo items
*	Added support for completion due dates for todo items (and are displayed within listview item)
* Uses a DialogFragment for creating and editing items
*	Added support for selecting the priority of each todo item. The priority is displayed as a color coded bar in the listview item.
*	Used a floating action bar and modified the colors to improve the UI.

Additional Functionality:

*	Added a checkbox to mark a task as completed. Added an option to clear all ‘completed’ tasks.
*	Added a share intent button to share the todo List items.

#Gif Walkthrough

Add Item:

![addItem.gif](https://github.com/sushmatn/TodoApp/blob/master/addItem.gif)


Edit Item:

![editItem.gif](https://github.com/sushmatn/TodoApp/blob/master/editItem.gif)

Persistence:

![persistence.gif](https://github.com/sushmatn/TodoApp/blob/master/persistence.gif)


Check completed tasks:

![clearCompleted.gif](https://github.com/sushmatn/TodoApp/blob/master/clearCompleted.gif)


Share the todo List:

![share.gif](https://github.com/sushmatn/TodoApp/blob/master/share.gif)
