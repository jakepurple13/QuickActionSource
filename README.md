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

// add onClickListener
reply.setQuickActionClickListener(new QuickActionOnClickListener() {
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

#### Using onOpenListener to change displayed text and icon
```
// Fetch icons
final Drawable isFavoriteIcon = context.getResources().getDrawable(R.drawable.ic_action_important);
final Drawable isNotFavoriteIcon = context.getResources().getDrawable(R.drawable.ic_action_not_important);

// Creating a new ActionItem
ActionItem favorite = new ActionItem("Add Favorite", isNotFavoriteIcon);

// adding onClickListener
favorite.setQuickActionClickListener(new QuickActionOnClickListener() {
    @Override
    public void onClick(ActionItem item, View view) {
        if (selectedUser != null) {
            selectedUser.setFavorite(!selectedUser.isFavorite());
        }
    }
});

// onOpenListener to change visual stuff before displaying
favorite.setQuickActionOnOpenListener(new QuickActionOnOpenListener() {
    @Override
    public void onOpen(ActionItem item) {
        if (selectedUser != null && selectedUser.isFavorite()) {
            item.setIcon(isFavoriteIcon);
            item.setTitle("Unfavorite");
        }
        else {
            item.setIcon(isNotFavoriteIcon);
            item.setTitle("Add Favorite");
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
