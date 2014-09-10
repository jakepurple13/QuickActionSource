![Example](https://raw.githubusercontent.com/SimonTheSourcerer/QuickActionSource/master/examples/example_1.png)

## Features
* Easy to setup 
* Working on SDK13 and above
* Working on smartphones and tablets
* Added graphics matching androids holo.light theme

## How to Use

#### Setup
```
// Setup a QuickActionBar
actionBar = new QuickActionBar(context);
// Set orientation (default: BOTTOM, possible: TOP, BOTTOM, LEFT, RIGHT)
actionBar.setOrientation(QuickActionBar.BOTTOM);

// Fetch icon
Drawable replyIcon = context.getResources().getDrawable(R.drawable.ic_action_reply);
// Create an action
ActionItem reply = new ActionItem("Reply", replyIcon);

// add onClickListner
reply.setQuickActionClickListner(new QuickActionOnClickListner() {
    @Override
    public void onClick(ActionItem item, View view) {
        // Do something
    }
});

// Adding buttons to the ActionBar
actionBar.addActionItem(reply);
```

#### Open the action dialog
```
actionBar.show(anchorView);
```

#### Using onOpenListner to change displayed text and icon
```
// Fetch icons
final Drawable favoriteIcon = context.getResources().getDrawable(R.drawable.ic_action_important);
final Drawable noFavoriteIcon = context.getResources().getDrawable(R.drawable.ic_action_not_important);

// Example with onClickListner and changing display depending the selected user
ActionItem favorite = new ActionItem("Favorite", noFavoriteIcon);

// onClickListner
favorite.setQuickActionClickListner(new QuickActionOnClickListner() {
    @Override
    public void onClick(ActionItem item, View view) {
        if (selectedUser != null) {
            selectedUser.setFavorite(!selectedUser.isFavorite());
        }
    }
});

// onOpenListner to change visual stuff before displaying
favorite.setQuickActionOnOpenListner(new QuickActionOnOpenListner() {
    @Override
    public void onOpen(ActionItem item) {
        if (selectedUser != null && selectedUser.isFavorite()) {
            item.setIcon(favoriteIcon);
            item.setTitle("Unfavorite");
        }
        else {
            item.setIcon(noFavoriteIcon);
            item.setTitle("Favorite");
        }
    }
});
```

### Useful settings
```
// Set maximum action dialog width in px
actionBar.setMaxWidth(500);

// Set minimum button width in px
actionItem.setMinWidth(150);
// Dialog won't close on clicking this button
actionItem.setSticky(true);
```
## Author
* Simon L - @SimonTheSourcerer

## Contributors
~

## Licensing
QuickActionSource is licenced under apache 2.0 license
