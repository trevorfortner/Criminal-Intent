# 12/26 (12PM-1:30PM)

Source: https://docs.oracle.com/javafx/2/api/javafx/scene/doc-files/cssref.html

- **uri** = url(*address*)
- url("../../Images/Image.png") would make the css file go back in its directory twice, look for an Images directory, then look in that for Image.png (much like cd ../)
- **size** = number with units of length (px = pixels, em = font-size, ex = x-height of font) or percentage (specified using % at end of number)
- length can also do in = inches, cm = centimeters, mm = millimeters, pt = points (1/72 of an inch), pc = picas (12 points)
- effects include drop shadows and inner shadows
- paints include colors and gradients (linear and radial)

## Font
- These can basically be called whenever -fx-font can be

| CSS Property   | Possible Values                                                                                                                                                                                      | Default | Comments                               |
|----------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------|----------------------------------------|
| -fx-font       | **font-size** **font-family** **font-weight** **font-style**                                                                                                                                         | inherit | Can do all four at once                |
| -fx-font-family| **String**                                                                                                                                                                                           | inherit | Font style (eg Courrier New)           |
| -fx-font-size  | **size**                                                                                                                                                                                             | inherit | Font size                              |
| -fx-font-style | normal, italic, oblique                                                                                                                                                                              | inherit | Can use this to italicize words        |
| -fx-font-weight| normal, bold, bolder, lighter, 100, 200, 300, 400, 500, 600, 700, 800, 900                                                                                                                           | inherit | "Fatness" of font basically (eg bold)  |

## Node
| CSS Property   | Possible Values                                                                                                                                                                                      | Default | Range         | Comments                               |
|----------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------|---------------|----------------------------------------|
| -fx-blend-mode | add, blue, color-burn, color-dodge, darken, difference, exclusion, green, hard-light, lighten, multiply, overlay, red, screen, soft-light, src-atop, src-in, src-out, src-over                       | null    |               | Basically the color blending           |
| -fx-cursor     | null, crosshair, default, hand, move, e-resize, h-resize, ne-resize, nw-resize, n-resize, se-resize, sw-resize, s-resize, w-resize, v-resize, text, wait, **url**                                    | null    |               | Allows mouse cursor modification       |
| -fx-effect     | **effect**                                                                                                                                                                                           | null    |               | Adding an effect to the Node           |
| -fx-opacity    | **number**                                                                                                                                                                                           | 1       | [0.0 ... 1.0] |                                        |
| -fx-rotate     | **number**                                                                                                                                                                                           | 0       |               | Angle of rotation in clockwise degrees |
| -fx-scale-x    | **number**                                                                                                                                                                                           | 1       |               | Scale about the center in x direction  |
| -fx-scale-y    | **number**                                                                                                                                                                                           | 1       |               | Scale about the center in y direction  |

## Labeled
- Extends Node (so can do all the CSS things that Node can, too)

| CSS Property        | Possible Values                                                                                                                                                                                      | Default | Comments                               |
|---------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------|----------------------------------------|
| -fx-alignment       | top-left, top-center, top-right, center-left, center, center-right bottom-left, bottom-center, bottom-right, baseline-left, baseline-center, baseline-right                                          | top-left| Allignment in the Labeled class        |
| -fx-text-alignment  | left, center, right, justify                                                                                                                                                                         | left    | Text allignment in the Labeled object  |
| -fx-text-overrun    | center-ellipsis, center-word-ellipsis, clip, ellipsis, leading-ellipsis, leading-word-ellipsis, word-ellipsis                                                                                        | ellipsis| What to put when text needs to wrap    |
| -fx-wrap-text       | **boolean**                                                                                                                                                                                          | false   | Whether or not to wrap the text        |
| -fx-font            | **font**                                                                                                                                                                                             | inherits| The font for the label                 |
| -fx-underline       | **boolean**                                                                                                                                                                                          | false   | Whether the label should underline     |
| -fx-graphic         | **uri**                                                                                                                                                                                              | null    | Graphic to display                     |
| -fx-graphic-text-gap| **size**                                                                                                                                                                                             | 4       | Gap between graphic and text           |
| -fx-label-padding   | **size**, **size** **size** **size** **size**                                                                                                                                                        | 0,0,0,0 | Gap between labels                     |
| -fx-text-fill       | **paint**                                                                                                                                                                                            | black   | Color of the text                      |
| -fx-ellipsis-string | **string**                                                                                                                                                                                           | ...     | Ellipsis to use when wrapping          |

## Label
- Extends Labeled
- No additional CSS functions

## Button
- Extends Labeled
- No additional CSS functions

## Region
- Extends Node
- There's quite a few more, but these are the ones I thought I might need: 

| CSS Property        | Possible Values                                                                                                                                                                                      | Default | Comments                               |
|---------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------|----------------------------------------|
| -fx-background-color| **paint**                                                                                                                                                                                            | null    | Sets the background color              |
| -fx-background-image| **uri**                                                                                                                                                                                              | null    | Sets the background to an image        |
| -fx-border-color    | **paint**                                                                                                                                                                                            | null    | Sets a border color                    |
| -fx-border-width    | **size**                                                                                                                                                                                             | null    | Sets border width                      |

## AnchorPane
- Extends Region
- No additional CSS functions

## ImageView
- Extends Node

| CSS Property        | Possible Values                                                                                                                                                                                      | Default |
|---------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------|
| -fx-image           | **uri**                                                                                                                                                                                              | null    |
