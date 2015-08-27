# Pre-work - *TodoApp*

**TodoApp** is an android app that allows building a todo list and basic todo items management functionality including adding new items, editing and deleting an existing item.

Submitted by: **Sushma Nayak**

Time spent: **16** hours spent in total

## User Stories

The following **required** functionality is completed:

* [x] User can **successfully add and remove items** from the todo list
* [x] User can **tap a todo item in the list and bring up an edit screen for the todo item** and then have any changes to the text reflected in the todo list.
* [x] User can **persist todo items** and retrieve them properly on app restart

The following **optional** features are implemented:

* [x] Persist the todo items [into SQLite](http://guides.codepath.com/android/Persisting-Data-to-the-Device#sqlite) instead of a text file
* [x] Improve style of the todo items in the list -> used a recyclerview [using a custom adapter](http://guides.codepath.com/android/Using-an-ArrayAdapter-with-ListView)
* [x] Add support for completion due dates for todo items (and display within listview item)
* [x] Use a [DialogFragment](http://guides.codepath.com/android/Using-DialogFragment) instead of new Activity for editing items
* [x] Add support for selecting the priority of each todo item (and display in listview item)
* [x] Tweak the style improving the UI / UX, play with colors, images or backgrounds

The following **additional** features are implemented:

* [x]	Added a checkbox to mark a task as completed. Added an option to clear all ‘completed’ tasks.
* [x]	Added a share intent button to share the todo List items.

## Video Walkthrough 

Here's a walkthrough of implemented user stories:

Edit Item:

![editItem.gif](https://github.com/sushmatn/TodoApp/blob/master/editItem.gif)


Check completed tasks:

![clearCompleted.gif](https://github.com/sushmatn/TodoApp/blob/master/clearCompleted.gif)


Share the todo List:

![share.gif](https://github.com/sushmatn/TodoApp/blob/master/share.gif)


Persistence:

![persistence.gif](https://github.com/sushmatn/TodoApp/blob/master/persistence.gif)


Add new Item:

![addItem.gif](https://github.com/sushmatn/TodoApp/blob/master/addItem.gif)


GIF created with [LiceCap](http://www.cockos.com/licecap/).

## Notes

I ran into a few problems:

* I used a recyclerview to display the list. I could not figure out how to display a different layout when the contents are empty (like the way it’s done in listview).

* I added eventhandlers to the individual views/viewgroups within the ViewHolder since I wanted different behaviors on the checkbox and the rest of the controls. But I am not sure if that is the right way to achieve that behavior.

* I created a 'share' menu item for sharing the contents of the todoList. When the share button is clicked, I am creating a new ACTION_SEND Intent and then calling it using startActivity.
I looked into using the ShareActionProvider, but I figured that I need to call setShareIntent every time an item is added/edited in the list which seemed unnecessary.

## License

    Copyright [2015] [Sushma Nayak]

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
