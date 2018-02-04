# adidas-api-tests
This was done as a part of Adidas Senior QA engineer job assessment process

## Instructions
1. Download the code on youe machine
2. Install `gradle` on your machine
3. In the terminal, navigate to the folder that contains `build.gradle` in this project
4. Run the command `gradle test`

## Reports
Reports can be viewed in the path `build/reports/tests/test/index.html`

## Some things to note
> I have added the assertion for the 1 second response time but when I run it locally, 
it always fails as the request takes more than a second. You can edit the value in code in `CMS.java` and either comment
out the time value in `sendGetRequest` method or comment out the line. for the purpose of assessment, I have left the time
validation to be there

>For the SLA to check for analytics_name, I found that not all components have the “analytics_name” in it and hence the test fails.
I am looking at the “analytics_name” found at the path:
component_presentations[0].component."content_fields.items[0].calls_to_action[0].supporting_fields.supporting_fields.standard_metadata
I realised that only the first and third component has “analytics_name” data but the remaining two components don’t, 
and hence the test fails

>Also, I wasn’t sure if you wanted me to check the exact path where “analytics_name” showed up so I have done a bit of both. 
When I get the whole component object, I first assert if “analytics_name” exists anywhere inside `items` array. 
If we wish to change it to check for it anywhere inside the `component` object, that should be pretty similar to how I have
done it to check for it inside `items` object.
If it doesn’t the test simply fails. If it does, then I go ahead and check for the exact path of it.
I noticed that there is “analytics_name” in a different path inside "background_media" media that haven’t checked for the purpose
of the test.

>I haven't spent a lot of time in building extendible framework as I had just a few assertions to make.
But I have tried to demostrate a few things like having values set in config file, abstracting code in methods (here I have
added methods to the same class file but for a bigger test suite, I group helper methods into different helper files)

>For validating the images, I just grabbed the image urls for desktop, tablet and mobile because I find it to be a cleaner way. I could have looked for all .jpg's in the response too but in case new image format's were added like svg etc, it would have failed to validate all of them.  

