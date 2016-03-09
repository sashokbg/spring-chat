# Asynchronous message board with Spring 4 and Servlet 3 - Long polling technique

Allows multiple users to connect a chat room and exchange instant messages, using long polling.
Manages timeouts for unactive users and automatically disconnects them.

A request is sent to the server only if a new message is available for the user or if a timeout has occured

Technologies

- Java 8
	- Functional programming
	- Stream API
	- Joda Time
	- ArrayBlockingQueue
	- ThreadPoolExecutor
- Servlet 3
	- Async Enabled
	- No web.xml
	- Annotation Config
- Spring MVC 4
	- Annotation Config
	- Async support
	- Validation
	- Formatting
	- JSON Serializer
	- locale resolver
	- message source
	- resource handler
	- DeferredResult


