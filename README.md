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

Some features:

* Generated code has no compilation dependencies;
* You can input source code (.java) or compiled classes (.class);
* Doesn't need any annotations, which makes flug extremely suitable for generated classes (such as JAXB);
* Generate clever collections setters (Map, List, and Set)

## How do I use it?

Flugs comes as a maven plugin, a runnable class, or a standalone command-line tool. More on that later.



