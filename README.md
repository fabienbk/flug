# flug
Fluent API generator for java beans

## What does it do?

Ever felt that instantiating and wiring non-trivial sets of java beans was incredibly tedious? Flug generates a fluent API derived from the beans you want to
build, so that you can create one-lines like this:

	new ProjectBuilder().name("My project").contributor().name("John").age(34).end().build();

Instead of :

	Project project = new Project().
	project.setName("My Project");
	Contributor contributor = new Contributor();
	contributor.setName("John");
	contributor.setAge(31);
	project.setContributor(contributor);

Flug generate pure java code that has no compilation dependencies. Flug also does not require that you put annotation on your beans, making it extremely suitable
for generated classes (via JAXB for instance).

