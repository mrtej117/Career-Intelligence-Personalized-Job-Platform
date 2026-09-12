package com.job.seed;

import com.job.enums.JobType;
import com.job.enums.WorkMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.job.enums.JobType.*;
import static com.job.enums.WorkMode.*;

/**
 * Provides 155+ realistic job definitions organized by category.
 * Each method returns jobs for a specific professional domain.
 */
public final class SeedJobDataProvider {

    private SeedJobDataProvider() {}

    public static List<SeedJobDefinition> allJobs() {
        List<SeedJobDefinition> jobs = new ArrayList<>();
        jobs.addAll(softwareDevelopmentJobs());
        jobs.addAll(aiMlJobs());
        jobs.addAll(dataJobs());
        jobs.addAll(cloudDevOpsJobs());
        jobs.addAll(cybersecurityJobs());
        jobs.addAll(mobileJobs());
        jobs.addAll(productBusinessJobs());
        jobs.addAll(designJobs());
        jobs.addAll(qaAndOtherJobs());
        return Collections.unmodifiableList(jobs);
    }

    /** Returns jobs for softwareDevelopmentJobs */
    public static List<SeedJobDefinition> softwareDevelopmentJobs() {
        return List.of(
        new SeedJobDefinition(
            "Software Engineer",
            "Work alongside talented engineers to deliver a world-class user experience. Help us build next-generation web applications with responsive and intuitive interfaces. We are seeking an expert developer to drive architectural decisions and write robust code. The ideal candidate thrives in a fast-paced setting.",
            "Amsterdam, Netherlands",
            FULL_TIME,
            REMOTE,
            List.of("Design robust, scalable and secure features.", "Drive continuous adoption and integration of relevant new technologies.", "Contribute in all phases of the development lifecycle.", "Collaborate with product managers and designers to deliver technical solutions.", "Architect and implement RESTful APIs and event-driven microservices."),
            List.of("JavaScript", "Django", "TypeScript", "Python", "Kubernetes", "React", "PostgreSQL", "Git", "SQL", "Java"),
            List.of("How many years of experience do you have with Git?"),
            72
        ),
        new SeedJobDefinition(
            "Software Engineer",
            "We are seeking an expert developer to drive architectural decisions and write robust code. Join our core engineering team to develop new features and improve system reliability. You'll play a key role in designing and implementing backend APIs used by millions. We offer a collaborative and innovative work environment.",
            "Austin, TX",
            PART_TIME,
            REMOTE,
            List.of("Architect and implement RESTful APIs and event-driven microservices.", "Follow best practices like test-driven development and continuous integration.", "Collaborate with product managers and designers to deliver technical solutions.", "Contribute in all phases of the development lifecycle.", "Optimize applications for maximum speed and scalability."),
            List.of("REST APIs", "React", "SQL", "GraphQL", "Java", "AWS", "Microservices", "PostgreSQL", "Kubernetes", "TypeScript"),
            List.of("How many years of experience do you have with GraphQL?"),
            39
        ),
        new SeedJobDefinition(
            "Senior Software Engineer",
            "Help us build next-generation web applications with responsive and intuitive interfaces. Take ownership of critical services and lead the transition to modern microservices. We offer a collaborative and innovative work environment.",
            "Singapore",
            PART_TIME,
            ONSITE,
            List.of("Architect and implement RESTful APIs and event-driven microservices.", "Write clean, maintainable, and efficient code.", "Collaborate with product managers and designers to deliver technical solutions.", "Refactor existing codebase to improve performance and reduce technical debt."),
            List.of("JavaScript", "TypeScript", "Microservices", "MongoDB", "GraphQL", "Docker", "Java", "Django", "Kubernetes"),
            List.of("How many years of experience do you have with Microservices?", "Describe a complex Senior Software Engineer project you worked on."),
            31
        ),
        new SeedJobDefinition(
            "Senior Software Engineer",
            "Take ownership of critical services and lead the transition to modern microservices. You'll play a key role in designing and implementing backend APIs used by millions. We are seeking an expert developer to drive architectural decisions and write robust code. The ideal candidate thrives in a fast-paced setting.",
            "San Francisco, CA",
            INTERNSHIP,
            ONSITE,
            List.of("Refactor existing codebase to improve performance and reduce technical debt.", "Optimize applications for maximum speed and scalability.", "Architect and implement RESTful APIs and event-driven microservices.", "Design robust, scalable and secure features.", "Drive continuous adoption and integration of relevant new technologies.", "Follow best practices like test-driven development and continuous integration."),
            List.of("Spring Boot", "SQL", "Django", "Java", "Microservices", "Python"),
            List.of("How many years of experience do you have with Django?", "What are your salary expectations?"),
            63
        ),
        new SeedJobDefinition(
            "Senior Software Engineer",
            "Work alongside talented engineers to deliver a world-class user experience. Take ownership of critical services and lead the transition to modern microservices. Join us to make a significant impact on our industry.",
            "Gurugram, India",
            INTERNSHIP,
            HYBRID,
            List.of("Contribute in all phases of the development lifecycle.", "Architect and implement RESTful APIs and event-driven microservices.", "Refactor existing codebase to improve performance and reduce technical debt.", "Mentor junior developers and conduct code reviews.", "Write clean, maintainable, and efficient code."),
            List.of("Python", "SQL", "TypeScript", "Git", "GraphQL", "PostgreSQL", "React", "Microservices", "Kubernetes"),
            List.of("How many years of experience do you have with Microservices?", "What are your salary expectations?"),
            7
        ),
        new SeedJobDefinition(
            "Full Stack Developer",
            "We are seeking an expert developer to drive architectural decisions and write robust code. Take ownership of critical services and lead the transition to modern microservices. You will be responsible for building high-performance, scalable software applications. We offer a collaborative and innovative work environment.",
            "Dublin, Ireland",
            FULL_TIME,
            HYBRID,
            List.of("Contribute in all phases of the development lifecycle.", "Refactor existing codebase to improve performance and reduce technical debt.", "Optimize applications for maximum speed and scalability.", "Follow best practices like test-driven development and continuous integration.", "Architect and implement RESTful APIs and event-driven microservices."),
            List.of("GraphQL", "AWS", "Node.js", "REST APIs", "Spring Boot", "Java", "Python", "PostgreSQL", "MongoDB"),
            List.of("How many years of experience do you have with REST APIs?", "What are your salary expectations?"),
            61
        ),
        new SeedJobDefinition(
            "Full Stack Developer",
            "Help us build next-generation web applications with responsive and intuitive interfaces. You'll play a key role in designing and implementing backend APIs used by millions. Take ownership of critical services and lead the transition to modern microservices. The ideal candidate thrives in a fast-paced setting.",
            "Mumbai, India",
            FULL_TIME,
            HYBRID,
            List.of("Contribute in all phases of the development lifecycle.", "Drive continuous adoption and integration of relevant new technologies.", "Design robust, scalable and secure features.", "Follow best practices like test-driven development and continuous integration.", "Collaborate with product managers and designers to deliver technical solutions.", "Refactor existing codebase to improve performance and reduce technical debt.", "Write clean, maintainable, and efficient code."),
            List.of("Git", "Spring Boot", "GraphQL", "PostgreSQL", "REST APIs", "Java", "MongoDB"),
            List.of("How many years of experience do you have with PostgreSQL?"),
            61
        ),
        new SeedJobDefinition(
            "Full Stack Developer",
            "Take ownership of critical services and lead the transition to modern microservices. You will be responsible for building high-performance, scalable software applications. The ideal candidate thrives in a fast-paced setting.",
            "Munich, Germany",
            INTERNSHIP,
            HYBRID,
            List.of("Follow best practices like test-driven development and continuous integration.", "Mentor junior developers and conduct code reviews.", "Optimize applications for maximum speed and scalability.", "Architect and implement RESTful APIs and event-driven microservices.", "Refactor existing codebase to improve performance and reduce technical debt.", "Drive continuous adoption and integration of relevant new technologies.", "Contribute in all phases of the development lifecycle."),
            List.of("Kubernetes", "Node.js", "Docker", "JavaScript", "GraphQL", "Django", "Python"),
            List.of("How many years of experience do you have with Django?", "Describe a complex Full Stack Developer project you worked on."),
            16
        ),
        new SeedJobDefinition(
            "Backend Developer",
            "Join our core engineering team to develop new features and improve system reliability. Take ownership of critical services and lead the transition to modern microservices. Work alongside talented engineers to deliver a world-class user experience. Join us to make a significant impact on our industry.",
            "Chennai, India",
            PART_TIME,
            HYBRID,
            List.of("Design robust, scalable and secure features.", "Architect and implement RESTful APIs and event-driven microservices.", "Contribute in all phases of the development lifecycle.", "Collaborate with product managers and designers to deliver technical solutions.", "Drive continuous adoption and integration of relevant new technologies.", "Follow best practices like test-driven development and continuous integration.", "Mentor junior developers and conduct code reviews."),
            List.of("React", "Django", "REST APIs", "TypeScript", "Node.js"),
            List.of("How many years of experience do you have with Django?", "What are your salary expectations?"),
            45
        ),
        new SeedJobDefinition(
            "Backend Developer",
            "Take ownership of critical services and lead the transition to modern microservices. You'll play a key role in designing and implementing backend APIs used by millions. Work alongside talented engineers to deliver a world-class user experience. You will be a key player in our growing, dynamic team.",
            "Melbourne, Australia",
            INTERNSHIP,
            ONSITE,
            List.of("Mentor junior developers and conduct code reviews.", "Write clean, maintainable, and efficient code.", "Drive continuous adoption and integration of relevant new technologies.", "Design robust, scalable and secure features.", "Refactor existing codebase to improve performance and reduce technical debt.", "Optimize applications for maximum speed and scalability."),
            List.of("Docker", "PostgreSQL", "REST APIs", "Django", "SQL", "React", "MongoDB", "Git"),
            List.of("How many years of experience do you have with MongoDB?", "What are your salary expectations?"),
            77
        ),
        new SeedJobDefinition(
            "Frontend Developer",
            "Take ownership of critical services and lead the transition to modern microservices. We are seeking an expert developer to drive architectural decisions and write robust code. We offer a collaborative and innovative work environment.",
            "Kolkata, India",
            FULL_TIME,
            HYBRID,
            List.of("Drive continuous adoption and integration of relevant new technologies.", "Design robust, scalable and secure features.", "Follow best practices like test-driven development and continuous integration.", "Mentor junior developers and conduct code reviews.", "Architect and implement RESTful APIs and event-driven microservices.", "Collaborate with product managers and designers to deliver technical solutions.", "Write clean, maintainable, and efficient code."),
            List.of("MongoDB", "Spring Boot", "TypeScript", "Microservices", "GraphQL"),
            List.of("How many years of experience do you have with TypeScript?", "What are your salary expectations?", "Describe a complex Frontend Developer project you worked on."),
            20
        ),
        new SeedJobDefinition(
            "Frontend Developer",
            "Work alongside talented engineers to deliver a world-class user experience. You'll play a key role in designing and implementing backend APIs used by millions. The ideal candidate thrives in a fast-paced setting.",
            "Kolkata, India",
            FULL_TIME,
            ONSITE,
            List.of("Mentor junior developers and conduct code reviews.", "Write clean, maintainable, and efficient code.", "Architect and implement RESTful APIs and event-driven microservices.", "Collaborate with product managers and designers to deliver technical solutions.", "Optimize applications for maximum speed and scalability."),
            List.of("React", "AWS", "Microservices", "REST APIs", "Docker", "JavaScript"),
            List.of("How many years of experience do you have with React?", "What are your salary expectations?"),
            29
        ),
        new SeedJobDefinition(
            "Java Developer",
            "You will be responsible for building high-performance, scalable software applications. Take ownership of critical services and lead the transition to modern microservices. We are seeking an expert developer to drive architectural decisions and write robust code. We offer a collaborative and innovative work environment.",
            "Sydney, Australia",
            FULL_TIME,
            HYBRID,
            List.of("Contribute in all phases of the development lifecycle.", "Mentor junior developers and conduct code reviews.", "Write clean, maintainable, and efficient code.", "Drive continuous adoption and integration of relevant new technologies.", "Follow best practices like test-driven development and continuous integration."),
            List.of("SQL", "Spring Boot", "React", "Git", "TypeScript"),
            List.of("How many years of experience do you have with SQL?", "What are your salary expectations?"),
            23
        ),
        new SeedJobDefinition(
            "Java Developer",
            "We are seeking an expert developer to drive architectural decisions and write robust code. You will be responsible for building high-performance, scalable software applications. Work alongside talented engineers to deliver a world-class user experience. The ideal candidate thrives in a fast-paced setting.",
            "Berlin, Germany",
            FULL_TIME,
            HYBRID,
            List.of("Drive continuous adoption and integration of relevant new technologies.", "Collaborate with product managers and designers to deliver technical solutions.", "Design robust, scalable and secure features.", "Mentor junior developers and conduct code reviews.", "Contribute in all phases of the development lifecycle.", "Write clean, maintainable, and efficient code.", "Refactor existing codebase to improve performance and reduce technical debt."),
            List.of("TypeScript", "SQL", "GraphQL", "Git", "Node.js", "PostgreSQL", "Spring Boot", "AWS"),
            List.of("How many years of experience do you have with Node.js?"),
            81
        ),
        new SeedJobDefinition(
            "Python Developer",
            "Join our core engineering team to develop new features and improve system reliability. You will be responsible for building high-performance, scalable software applications. Take ownership of critical services and lead the transition to modern microservices. Join us to make a significant impact on our industry.",
            "Delhi, India",
            FULL_TIME,
            HYBRID,
            List.of("Design robust, scalable and secure features.", "Write clean, maintainable, and efficient code.", "Mentor junior developers and conduct code reviews.", "Collaborate with product managers and designers to deliver technical solutions.", "Architect and implement RESTful APIs and event-driven microservices.", "Refactor existing codebase to improve performance and reduce technical debt.", "Optimize applications for maximum speed and scalability."),
            List.of("MongoDB", "Java", "REST APIs", "AWS", "JavaScript", "PostgreSQL", "Git", "Node.js", "Docker"),
            List.of("How many years of experience do you have with AWS?", "What are your salary expectations?", "Describe a complex Python Developer project you worked on."),
            54
        ),
        new SeedJobDefinition(
            "Python Developer",
            "You'll play a key role in designing and implementing backend APIs used by millions. Join our core engineering team to develop new features and improve system reliability. Join us to make a significant impact on our industry.",
            "Kochi, India",
            FULL_TIME,
            REMOTE,
            List.of("Refactor existing codebase to improve performance and reduce technical debt.", "Collaborate with product managers and designers to deliver technical solutions.", "Design robust, scalable and secure features.", "Contribute in all phases of the development lifecycle."),
            List.of("Java", "Docker", "Kubernetes", "Node.js", "TypeScript", "PostgreSQL", "SQL", "Spring Boot", "REST APIs", "React"),
            List.of("How many years of experience do you have with Kubernetes?"),
            63
        ),
        new SeedJobDefinition(
            "Node.js Developer",
            "Join our core engineering team to develop new features and improve system reliability. Take ownership of critical services and lead the transition to modern microservices. We are seeking an expert developer to drive architectural decisions and write robust code. We offer a collaborative and innovative work environment.",
            "Visakhapatnam, India",
            FULL_TIME,
            ONSITE,
            List.of("Refactor existing codebase to improve performance and reduce technical debt.", "Optimize applications for maximum speed and scalability.", "Mentor junior developers and conduct code reviews.", "Contribute in all phases of the development lifecycle.", "Follow best practices like test-driven development and continuous integration.", "Write clean, maintainable, and efficient code."),
            List.of("Spring Boot", "REST APIs", "JavaScript", "Docker", "Git", "Microservices"),
            List.of("How many years of experience do you have with Git?", "What are your salary expectations?"),
            19
        ),
        new SeedJobDefinition(
            "React Developer",
            "We are seeking an expert developer to drive architectural decisions and write robust code. You will be responsible for building high-performance, scalable software applications. Join us to make a significant impact on our industry.",
            "Paris, France",
            CONTRACT,
            ONSITE,
            List.of("Contribute in all phases of the development lifecycle.", "Design robust, scalable and secure features.", "Collaborate with product managers and designers to deliver technical solutions.", "Mentor junior developers and conduct code reviews.", "Optimize applications for maximum speed and scalability.", "Drive continuous adoption and integration of relevant new technologies."),
            List.of("Django", "AWS", "Docker", "REST APIs", "Python", "React", "Java", "Git", "Spring Boot", "Microservices"),
            List.of("How many years of experience do you have with React?", "What are your salary expectations?"),
            88
        ),
        new SeedJobDefinition(
            "React Developer",
            "Help us build next-generation web applications with responsive and intuitive interfaces. We are seeking an expert developer to drive architectural decisions and write robust code. You'll play a key role in designing and implementing backend APIs used by millions. The ideal candidate thrives in a fast-paced setting.",
            "Noida, India",
            FULL_TIME,
            REMOTE,
            List.of("Mentor junior developers and conduct code reviews.", "Contribute in all phases of the development lifecycle.", "Design robust, scalable and secure features.", "Optimize applications for maximum speed and scalability.", "Write clean, maintainable, and efficient code.", "Architect and implement RESTful APIs and event-driven microservices.", "Refactor existing codebase to improve performance and reduce technical debt."),
            List.of("Python", "AWS", "JavaScript", "REST APIs", "React", "Django", "Kubernetes", "Spring Boot"),
            List.of("How many years of experience do you have with React?"),
            44
        ),
        new SeedJobDefinition(
            ".NET Developer",
            "You will be responsible for building high-performance, scalable software applications. We are seeking an expert developer to drive architectural decisions and write robust code. You'll play a key role in designing and implementing backend APIs used by millions. You will be a key player in our growing, dynamic team.",
            "New York, NY",
            FULL_TIME,
            ONSITE,
            List.of("Drive continuous adoption and integration of relevant new technologies.", "Collaborate with product managers and designers to deliver technical solutions.", "Write clean, maintainable, and efficient code.", "Mentor junior developers and conduct code reviews.", "Design robust, scalable and secure features.", "Architect and implement RESTful APIs and event-driven microservices.", "Refactor existing codebase to improve performance and reduce technical debt."),
            List.of("MongoDB", "JavaScript", "React", "Python", "Kubernetes", "TypeScript", "Git", "AWS", "REST APIs", "PostgreSQL"),
            List.of("How many years of experience do you have with TypeScript?"),
            11
        ),
        new SeedJobDefinition(
            "Golang Developer",
            "We are seeking an expert developer to drive architectural decisions and write robust code. Take ownership of critical services and lead the transition to modern microservices. We offer a collaborative and innovative work environment.",
            "Visakhapatnam, India",
            FULL_TIME,
            ONSITE,
            List.of("Optimize applications for maximum speed and scalability.", "Refactor existing codebase to improve performance and reduce technical debt.", "Write clean, maintainable, and efficient code.", "Architect and implement RESTful APIs and event-driven microservices.", "Collaborate with product managers and designers to deliver technical solutions.", "Design robust, scalable and secure features."),
            List.of("TypeScript", "JavaScript", "AWS", "Django", "Microservices", "Java", "SQL"),
            List.of("How many years of experience do you have with Microservices?"),
            16
        ),
        new SeedJobDefinition(
            "Software Architect",
            "We are seeking an expert developer to drive architectural decisions and write robust code. Join our core engineering team to develop new features and improve system reliability. The ideal candidate thrives in a fast-paced setting.",
            "Singapore",
            INTERNSHIP,
            REMOTE,
            List.of("Drive continuous adoption and integration of relevant new technologies.", "Write clean, maintainable, and efficient code.", "Optimize applications for maximum speed and scalability.", "Architect and implement RESTful APIs and event-driven microservices.", "Contribute in all phases of the development lifecycle.", "Mentor junior developers and conduct code reviews."),
            List.of("PostgreSQL", "REST APIs", "SQL", "React", "Kubernetes", "MongoDB", "Java", "AWS", "GraphQL"),
            List.of("How many years of experience do you have with React?", "What are your salary expectations?"),
            63
        ),
        new SeedJobDefinition(
            "Software Architect",
            "Join our core engineering team to develop new features and improve system reliability. You'll play a key role in designing and implementing backend APIs used by millions. We are seeking an expert developer to drive architectural decisions and write robust code. Join us to make a significant impact on our industry.",
            "Boston, MA",
            FULL_TIME,
            ONSITE,
            List.of("Design robust, scalable and secure features.", "Write clean, maintainable, and efficient code.", "Refactor existing codebase to improve performance and reduce technical debt.", "Collaborate with product managers and designers to deliver technical solutions.", "Follow best practices like test-driven development and continuous integration."),
            List.of("AWS", "Docker", "Java", "Spring Boot", "Kubernetes", "Node.js", "PostgreSQL", "REST APIs"),
            List.of("How many years of experience do you have with Kubernetes?", "Describe a complex Software Architect project you worked on."),
            39
        ),
        new SeedJobDefinition(
            "Principal Engineer",
            "Help us build next-generation web applications with responsive and intuitive interfaces. You'll play a key role in designing and implementing backend APIs used by millions. Join our core engineering team to develop new features and improve system reliability. The ideal candidate thrives in a fast-paced setting.",
            "Visakhapatnam, India",
            FULL_TIME,
            REMOTE,
            List.of("Write clean, maintainable, and efficient code.", "Contribute in all phases of the development lifecycle.", "Design robust, scalable and secure features.", "Mentor junior developers and conduct code reviews.", "Optimize applications for maximum speed and scalability.", "Collaborate with product managers and designers to deliver technical solutions.", "Follow best practices like test-driven development and continuous integration."),
            List.of("Git", "GraphQL", "Kubernetes", "Django", "TypeScript", "AWS", "Docker", "Python"),
            List.of("How many years of experience do you have with Kubernetes?"),
            52
        ),
        new SeedJobDefinition(
            "Staff Software Engineer",
            "Take ownership of critical services and lead the transition to modern microservices. Help us build next-generation web applications with responsive and intuitive interfaces. Join us to make a significant impact on our industry.",
            "Munich, Germany",
            INTERNSHIP,
            ONSITE,
            List.of("Design robust, scalable and secure features.", "Mentor junior developers and conduct code reviews.", "Follow best practices like test-driven development and continuous integration.", "Contribute in all phases of the development lifecycle.", "Drive continuous adoption and integration of relevant new technologies.", "Architect and implement RESTful APIs and event-driven microservices."),
            List.of("Django", "Kubernetes", "Microservices", "Spring Boot", "GraphQL", "Docker"),
            List.of("How many years of experience do you have with Spring Boot?", "Describe a complex Staff Software Engineer project you worked on."),
            34
        ),
        new SeedJobDefinition(
            "Lead Developer",
            "Work alongside talented engineers to deliver a world-class user experience. Join our core engineering team to develop new features and improve system reliability. Join us to make a significant impact on our industry.",
            "Noida, India",
            PART_TIME,
            REMOTE,
            List.of("Design robust, scalable and secure features.", "Mentor junior developers and conduct code reviews.", "Follow best practices like test-driven development and continuous integration.", "Architect and implement RESTful APIs and event-driven microservices.", "Collaborate with product managers and designers to deliver technical solutions.", "Write clean, maintainable, and efficient code.", "Drive continuous adoption and integration of relevant new technologies."),
            List.of("React", "Microservices", "TypeScript", "JavaScript", "Node.js", "Git", "Python"),
            List.of("How many years of experience do you have with Git?", "What are your salary expectations?"),
            65
        ),
        new SeedJobDefinition(
            "Cloud Native Developer",
            "Help us build next-generation web applications with responsive and intuitive interfaces. Work alongside talented engineers to deliver a world-class user experience. You'll play a key role in designing and implementing backend APIs used by millions. You will be a key player in our growing, dynamic team.",
            "San Diego, CA",
            FULL_TIME,
            ONSITE,
            List.of("Drive continuous adoption and integration of relevant new technologies.", "Mentor junior developers and conduct code reviews.", "Contribute in all phases of the development lifecycle.", "Refactor existing codebase to improve performance and reduce technical debt.", "Optimize applications for maximum speed and scalability."),
            List.of("Spring Boot", "TypeScript", "SQL", "Node.js", "MongoDB", "Python"),
            List.of("How many years of experience do you have with Python?", "Describe a complex Cloud Native Developer project you worked on."),
            60
        )
        );
    }

    /** Returns jobs for aiMlJobs */
    public static List<SeedJobDefinition> aiMlJobs() {
        return List.of(
        new SeedJobDefinition(
            "AI Engineer",
            "You will design, develop, and deploy state-of-the-art ML models to solve real-world problems. Help us push the boundaries of artificial intelligence and machine learning. Join us to make a significant impact on our industry.",
            "New York, NY",
            INTERNSHIP,
            REMOTE,
            List.of("Stay up-to-date with the latest AI research and apply it to our products.", "Perform statistical analysis and fine-tuning using test results.", "Collaborate with data engineers to build robust data processing workflows.", "Train, tune, and evaluate deep neural networks."),
            List.of("NumPy", "Docker", "PyTorch", "Computer Vision", "SQL", "Python", "MLflow", "LLMs", "TensorFlow"),
            List.of("How many years of experience do you have with SQL?"),
            30
        ),
        new SeedJobDefinition(
            "AI Engineer",
            "Drive the development of intelligent applications powered by natural language processing. You will design, develop, and deploy state-of-the-art ML models to solve real-world problems. Join us to make a significant impact on our industry.",
            "Chennai, India",
            CONTRACT,
            ONSITE,
            List.of("Train, tune, and evaluate deep neural networks.", "Deploy ML models to production environments using containerization.", "Collaborate with data engineers to build robust data processing workflows.", "Perform statistical analysis and fine-tuning using test results.", "Develop NLP algorithms and tools for unstructured text analysis."),
            List.of("NumPy", "Keras", "Pandas", "TensorFlow", "Python", "AWS SageMaker", "Docker", "Scikit-Learn"),
            List.of("How many years of experience do you have with Docker?", "Describe a complex AI Engineer project you worked on."),
            19
        ),
        new SeedJobDefinition(
            "Machine Learning Engineer",
            "We need an experienced professional to bridge the gap between ML research and scalable production systems. Work on cutting-edge computer vision systems for autonomous processing. Join our AI lab to research and implement generative models and deep learning architectures. You will be a key player in our growing, dynamic team.",
            "Visakhapatnam, India",
            CONTRACT,
            ONSITE,
            List.of("Optimize ML models for latency and throughput in real-time systems.", "Stay up-to-date with the latest AI research and apply it to our products.", "Perform statistical analysis and fine-tuning using test results.", "Develop NLP algorithms and tools for unstructured text analysis."),
            List.of("Docker", "NLP", "NumPy", "Python", "PyTorch", "Keras", "Computer Vision", "Hugging Face", "MLflow"),
            List.of("How many years of experience do you have with Computer Vision?"),
            32
        ),
        new SeedJobDefinition(
            "Machine Learning Engineer",
            "Work on cutting-edge computer vision systems for autonomous processing. Help us push the boundaries of artificial intelligence and machine learning. You will be a key player in our growing, dynamic team.",
            "Los Angeles, CA",
            FULL_TIME,
            HYBRID,
            List.of("Train, tune, and evaluate deep neural networks.", "Deploy ML models to production environments using containerization.", "Design and implement machine learning pipelines and predictive models.", "Collaborate with data engineers to build robust data processing workflows.", "Develop NLP algorithms and tools for unstructured text analysis.", "Perform statistical analysis and fine-tuning using test results."),
            List.of("LLMs", "Scikit-Learn", "Python", "Pandas", "TensorFlow"),
            List.of("How many years of experience do you have with Scikit-Learn?"),
            76
        ),
        new SeedJobDefinition(
            "Machine Learning Engineer",
            "Work on cutting-edge computer vision systems for autonomous processing. Join our AI lab to research and implement generative models and deep learning architectures. The ideal candidate thrives in a fast-paced setting.",
            "San Francisco, CA",
            FULL_TIME,
            REMOTE,
            List.of("Stay up-to-date with the latest AI research and apply it to our products.", "Deploy ML models to production environments using containerization.", "Train, tune, and evaluate deep neural networks.", "Optimize ML models for latency and throughput in real-time systems.", "Perform statistical analysis and fine-tuning using test results."),
            List.of("Docker", "TensorFlow", "PyTorch", "AWS SageMaker", "LLMs", "SQL"),
            List.of("How many years of experience do you have with PyTorch?", "Describe a complex Machine Learning Engineer project you worked on."),
            79
        ),
        new SeedJobDefinition(
            "Generative AI Engineer",
            "Help us push the boundaries of artificial intelligence and machine learning. Drive the development of intelligent applications powered by natural language processing. We need an experienced professional to bridge the gap between ML research and scalable production systems. The ideal candidate thrives in a fast-paced setting.",
            "San Francisco, CA",
            INTERNSHIP,
            ONSITE,
            List.of("Develop NLP algorithms and tools for unstructured text analysis.", "Design and implement machine learning pipelines and predictive models.", "Collaborate with data engineers to build robust data processing workflows.", "Train, tune, and evaluate deep neural networks.", "Deploy ML models to production environments using containerization.", "Perform statistical analysis and fine-tuning using test results."),
            List.of("LLMs", "Scikit-Learn", "Docker", "SQL", "TensorFlow", "AWS SageMaker", "Keras"),
            List.of("How many years of experience do you have with LLMs?", "What are your salary expectations?"),
            29
        ),
        new SeedJobDefinition(
            "Generative AI Engineer",
            "Join our AI lab to research and implement generative models and deep learning architectures. Drive the development of intelligent applications powered by natural language processing. You will design, develop, and deploy state-of-the-art ML models to solve real-world problems. The ideal candidate thrives in a fast-paced setting.",
            "Abu Dhabi, UAE",
            INTERNSHIP,
            REMOTE,
            List.of("Design and implement machine learning pipelines and predictive models.", "Train, tune, and evaluate deep neural networks.", "Stay up-to-date with the latest AI research and apply it to our products.", "Optimize ML models for latency and throughput in real-time systems.", "Deploy ML models to production environments using containerization.", "Develop NLP algorithms and tools for unstructured text analysis.", "Perform statistical analysis and fine-tuning using test results."),
            List.of("NumPy", "OpenCV", "AWS SageMaker", "MLflow", "TensorFlow", "Pandas", "Python", "Scikit-Learn", "NLP", "Computer Vision"),
            List.of("How many years of experience do you have with AWS SageMaker?", "What are your salary expectations?", "Describe a complex Generative AI Engineer project you worked on."),
            38
        ),
        new SeedJobDefinition(
            "NLP Engineer",
            "Join our AI lab to research and implement generative models and deep learning architectures. Drive the development of intelligent applications powered by natural language processing. Join us to make a significant impact on our industry.",
            "Delhi, India",
            INTERNSHIP,
            REMOTE,
            List.of("Develop NLP algorithms and tools for unstructured text analysis.", "Collaborate with data engineers to build robust data processing workflows.", "Stay up-to-date with the latest AI research and apply it to our products.", "Train, tune, and evaluate deep neural networks."),
            List.of("OpenCV", "NumPy", "Python", "Docker", "Scikit-Learn", "LLMs", "Pandas", "TensorFlow", "Keras"),
            List.of("How many years of experience do you have with Keras?", "Describe a complex NLP Engineer project you worked on."),
            7
        ),
        new SeedJobDefinition(
            "NLP Engineer",
            "We need an experienced professional to bridge the gap between ML research and scalable production systems. You will design, develop, and deploy state-of-the-art ML models to solve real-world problems. You will be a key player in our growing, dynamic team.",
            "Amsterdam, Netherlands",
            PART_TIME,
            ONSITE,
            List.of("Train, tune, and evaluate deep neural networks.", "Stay up-to-date with the latest AI research and apply it to our products.", "Deploy ML models to production environments using containerization.", "Collaborate with data engineers to build robust data processing workflows.", "Optimize ML models for latency and throughput in real-time systems."),
            List.of("LLMs", "Pandas", "Keras", "Python", "MLflow"),
            List.of("How many years of experience do you have with Keras?", "What are your salary expectations?"),
            64
        ),
        new SeedJobDefinition(
            "Computer Vision Engineer",
            "You will design, develop, and deploy state-of-the-art ML models to solve real-world problems. We need an experienced professional to bridge the gap between ML research and scalable production systems. The ideal candidate thrives in a fast-paced setting.",
            "Bengaluru, India",
            FULL_TIME,
            ONSITE,
            List.of("Perform statistical analysis and fine-tuning using test results.", "Develop NLP algorithms and tools for unstructured text analysis.", "Collaborate with data engineers to build robust data processing workflows.", "Deploy ML models to production environments using containerization."),
            List.of("Hugging Face", "SQL", "LLMs", "PyTorch", "Docker"),
            List.of("How many years of experience do you have with Docker?", "Describe a complex Computer Vision Engineer project you worked on."),
            5
        ),
        new SeedJobDefinition(
            "Computer Vision Engineer",
            "Drive the development of intelligent applications powered by natural language processing. We need an experienced professional to bridge the gap between ML research and scalable production systems. You will design, develop, and deploy state-of-the-art ML models to solve real-world problems. The ideal candidate thrives in a fast-paced setting.",
            "Kochi, India",
            FULL_TIME,
            HYBRID,
            List.of("Train, tune, and evaluate deep neural networks.", "Stay up-to-date with the latest AI research and apply it to our products.", "Collaborate with data engineers to build robust data processing workflows.", "Design and implement machine learning pipelines and predictive models."),
            List.of("AWS SageMaker", "Docker", "Keras", "OpenCV", "TensorFlow", "MLflow", "Scikit-Learn", "Python"),
            List.of("How many years of experience do you have with AWS SageMaker?"),
            54
        ),
        new SeedJobDefinition(
            "MLOps Engineer",
            "You will design, develop, and deploy state-of-the-art ML models to solve real-world problems. We need an experienced professional to bridge the gap between ML research and scalable production systems. Drive the development of intelligent applications powered by natural language processing. We offer a collaborative and innovative work environment.",
            "Melbourne, Australia",
            FULL_TIME,
            REMOTE,
            List.of("Train, tune, and evaluate deep neural networks.", "Deploy ML models to production environments using containerization.", "Stay up-to-date with the latest AI research and apply it to our products.", "Optimize ML models for latency and throughput in real-time systems.", "Develop NLP algorithms and tools for unstructured text analysis.", "Collaborate with data engineers to build robust data processing workflows."),
            List.of("Computer Vision", "OpenCV", "Keras", "Python", "LLMs", "PyTorch", "Hugging Face", "NLP"),
            List.of("How many years of experience do you have with NLP?", "Describe a complex MLOps Engineer project you worked on."),
            77
        ),
        new SeedJobDefinition(
            "MLOps Engineer",
            "Drive the development of intelligent applications powered by natural language processing. Help us push the boundaries of artificial intelligence and machine learning. Join us to make a significant impact on our industry.",
            "San Francisco, CA",
            FULL_TIME,
            REMOTE,
            List.of("Perform statistical analysis and fine-tuning using test results.", "Deploy ML models to production environments using containerization.", "Develop NLP algorithms and tools for unstructured text analysis.", "Train, tune, and evaluate deep neural networks.", "Stay up-to-date with the latest AI research and apply it to our products.", "Optimize ML models for latency and throughput in real-time systems."),
            List.of("Scikit-Learn", "LLMs", "Computer Vision", "TensorFlow", "Python", "OpenCV", "AWS SageMaker"),
            List.of("How many years of experience do you have with Scikit-Learn?", "What are your salary expectations?", "Describe a complex MLOps Engineer project you worked on."),
            1
        ),
        new SeedJobDefinition(
            "AI Research Engineer",
            "Help us push the boundaries of artificial intelligence and machine learning. Join our AI lab to research and implement generative models and deep learning architectures. Drive the development of intelligent applications powered by natural language processing. Join us to make a significant impact on our industry.",
            "Atlanta, GA",
            PART_TIME,
            HYBRID,
            List.of("Train, tune, and evaluate deep neural networks.", "Deploy ML models to production environments using containerization.", "Stay up-to-date with the latest AI research and apply it to our products.", "Collaborate with data engineers to build robust data processing workflows."),
            List.of("PyTorch", "Keras", "Pandas", "SQL", "Scikit-Learn", "Docker"),
            List.of("How many years of experience do you have with Docker?"),
            11
        ),
        new SeedJobDefinition(
            "Deep Learning Engineer",
            "Work on cutting-edge computer vision systems for autonomous processing. You will design, develop, and deploy state-of-the-art ML models to solve real-world problems. Join us to make a significant impact on our industry.",
            "Los Angeles, CA",
            PART_TIME,
            HYBRID,
            List.of("Deploy ML models to production environments using containerization.", "Develop NLP algorithms and tools for unstructured text analysis.", "Train, tune, and evaluate deep neural networks.", "Collaborate with data engineers to build robust data processing workflows.", "Perform statistical analysis and fine-tuning using test results.", "Optimize ML models for latency and throughput in real-time systems."),
            List.of("LLMs", "PyTorch", "AWS SageMaker", "Docker", "SQL", "Scikit-Learn", "Pandas"),
            List.of("How many years of experience do you have with AWS SageMaker?", "What are your salary expectations?"),
            34
        ),
        new SeedJobDefinition(
            "Deep Learning Engineer",
            "Help us push the boundaries of artificial intelligence and machine learning. We need an experienced professional to bridge the gap between ML research and scalable production systems. We offer a collaborative and innovative work environment.",
            "Stockholm, Sweden",
            PART_TIME,
            REMOTE,
            List.of("Train, tune, and evaluate deep neural networks.", "Perform statistical analysis and fine-tuning using test results.", "Deploy ML models to production environments using containerization.", "Design and implement machine learning pipelines and predictive models.", "Collaborate with data engineers to build robust data processing workflows.", "Develop NLP algorithms and tools for unstructured text analysis.", "Stay up-to-date with the latest AI research and apply it to our products."),
            List.of("AWS SageMaker", "Scikit-Learn", "PyTorch", "Python", "TensorFlow", "Keras"),
            List.of("How many years of experience do you have with TensorFlow?", "What are your salary expectations?"),
            19
        ),
        new SeedJobDefinition(
            "Data Scientist",
            "We need an experienced professional to bridge the gap between ML research and scalable production systems. Help us push the boundaries of artificial intelligence and machine learning. Join our AI lab to research and implement generative models and deep learning architectures. You will be a key player in our growing, dynamic team.",
            "Munich, Germany",
            CONTRACT,
            REMOTE,
            List.of("Design and implement machine learning pipelines and predictive models.", "Develop NLP algorithms and tools for unstructured text analysis.", "Perform statistical analysis and fine-tuning using test results.", "Deploy ML models to production environments using containerization.", "Stay up-to-date with the latest AI research and apply it to our products.", "Collaborate with data engineers to build robust data processing workflows."),
            List.of("TensorFlow", "Docker", "LLMs", "NumPy", "MLflow", "SQL"),
            List.of("How many years of experience do you have with SQL?", "Describe a complex Data Scientist project you worked on."),
            74
        ),
        new SeedJobDefinition(
            "Data Scientist",
            "Drive the development of intelligent applications powered by natural language processing. Help us push the boundaries of artificial intelligence and machine learning. You will design, develop, and deploy state-of-the-art ML models to solve real-world problems. You will be a key player in our growing, dynamic team.",
            "Chicago, IL",
            FULL_TIME,
            HYBRID,
            List.of("Optimize ML models for latency and throughput in real-time systems.", "Train, tune, and evaluate deep neural networks.", "Stay up-to-date with the latest AI research and apply it to our products.", "Perform statistical analysis and fine-tuning using test results.", "Develop NLP algorithms and tools for unstructured text analysis.", "Design and implement machine learning pipelines and predictive models."),
            List.of("Computer Vision", "Pandas", "Docker", "Scikit-Learn", "PyTorch"),
            List.of("How many years of experience do you have with Computer Vision?"),
            62
        ),
        new SeedJobDefinition(
            "Data Scientist",
            "Drive the development of intelligent applications powered by natural language processing. You will design, develop, and deploy state-of-the-art ML models to solve real-world problems. Help us push the boundaries of artificial intelligence and machine learning. You will be a key player in our growing, dynamic team.",
            "Seattle, WA",
            INTERNSHIP,
            REMOTE,
            List.of("Deploy ML models to production environments using containerization.", "Train, tune, and evaluate deep neural networks.", "Collaborate with data engineers to build robust data processing workflows.", "Stay up-to-date with the latest AI research and apply it to our products.", "Perform statistical analysis and fine-tuning using test results."),
            List.of("Docker", "MLflow", "Computer Vision", "Pandas", "LLMs"),
            List.of("How many years of experience do you have with Pandas?"),
            49
        ),
        new SeedJobDefinition(
            "Applied AI Engineer",
            "You will design, develop, and deploy state-of-the-art ML models to solve real-world problems. We need an experienced professional to bridge the gap between ML research and scalable production systems. The ideal candidate thrives in a fast-paced setting.",
            "Denver, CO",
            INTERNSHIP,
            ONSITE,
            List.of("Develop NLP algorithms and tools for unstructured text analysis.", "Train, tune, and evaluate deep neural networks.", "Stay up-to-date with the latest AI research and apply it to our products.", "Deploy ML models to production environments using containerization.", "Collaborate with data engineers to build robust data processing workflows."),
            List.of("LLMs", "Scikit-Learn", "Pandas", "Python", "AWS SageMaker", "TensorFlow", "Hugging Face", "PyTorch", "Computer Vision"),
            List.of("How many years of experience do you have with Scikit-Learn?", "What are your salary expectations?"),
            16
        ),
        new SeedJobDefinition(
            "Senior ML Engineer",
            "Help us push the boundaries of artificial intelligence and machine learning. Work on cutting-edge computer vision systems for autonomous processing. You will be a key player in our growing, dynamic team.",
            "London, UK",
            CONTRACT,
            REMOTE,
            List.of("Stay up-to-date with the latest AI research and apply it to our products.", "Collaborate with data engineers to build robust data processing workflows.", "Design and implement machine learning pipelines and predictive models.", "Perform statistical analysis and fine-tuning using test results.", "Deploy ML models to production environments using containerization.", "Optimize ML models for latency and throughput in real-time systems.", "Develop NLP algorithms and tools for unstructured text analysis."),
            List.of("TensorFlow", "Hugging Face", "NumPy", "Keras", "Python", "AWS SageMaker", "PyTorch"),
            List.of("How many years of experience do you have with Hugging Face?"),
            79
        ),
        new SeedJobDefinition(
            "Lead AI Researcher",
            "Work on cutting-edge computer vision systems for autonomous processing. Join our AI lab to research and implement generative models and deep learning architectures. Help us push the boundaries of artificial intelligence and machine learning. We offer a collaborative and innovative work environment.",
            "Singapore",
            FULL_TIME,
            ONSITE,
            List.of("Perform statistical analysis and fine-tuning using test results.", "Collaborate with data engineers to build robust data processing workflows.", "Develop NLP algorithms and tools for unstructured text analysis.", "Design and implement machine learning pipelines and predictive models.", "Optimize ML models for latency and throughput in real-time systems."),
            List.of("SQL", "MLflow", "PyTorch", "AWS SageMaker", "Hugging Face", "Computer Vision"),
            List.of("How many years of experience do you have with Computer Vision?"),
            69
        )
        );
    }

    /** Returns jobs for dataJobs */
    public static List<SeedJobDefinition> dataJobs() {
        return List.of(
        new SeedJobDefinition(
            "Data Analyst",
            "Turn raw data into actionable insights to drive business strategy. Work closely with stakeholders to understand their data needs and deliver effective analytics. Join us to make a significant impact on our industry.",
            "Singapore",
            FULL_TIME,
            ONSITE,
            List.of("Perform complex SQL queries to extract insights from large datasets.", "Create comprehensive dashboards and visualizations for stakeholders.", "Optimize database performance and troubleshoot complex issues.", "Design, build, and maintain scalable automated data pipelines.", "Collaborate with data scientists to prepare data for ML models.", "Ensure data integrity, security, and compliance across all systems.", "Implement ETL/ELT processes using modern data integration tools."),
            List.of("dbt", "Snowflake", "Tableau", "BigQuery", "Python", "NoSQL", "Data Modeling"),
            List.of("How many years of experience do you have with Tableau?", "What are your salary expectations?"),
            25
        ),
        new SeedJobDefinition(
            "Data Analyst",
            "Work closely with stakeholders to understand their data needs and deliver effective analytics. Join our data team to manage complex ETL processes and ensure data quality. You will be a key player in our growing, dynamic team.",
            "Ahmedabad, India",
            INTERNSHIP,
            HYBRID,
            List.of("Ensure data integrity, security, and compliance across all systems.", "Design, build, and maintain scalable automated data pipelines.", "Create comprehensive dashboards and visualizations for stakeholders.", "Optimize database performance and troubleshoot complex issues.", "Architect and manage data warehouse solutions in the cloud."),
            List.of("Data Modeling", "dbt", "Airflow", "PostgreSQL", "R", "Hadoop", "Kafka", "SQL", "Python", "NoSQL"),
            List.of("How many years of experience do you have with Hadoop?", "What are your salary expectations?"),
            82
        ),
        new SeedJobDefinition(
            "Data Analyst",
            "Design and maintain scalable data pipelines and data warehouse infrastructure. Turn raw data into actionable insights to drive business strategy. Work closely with stakeholders to understand their data needs and deliver effective analytics. The ideal candidate thrives in a fast-paced setting.",
            "Paris, France",
            FULL_TIME,
            HYBRID,
            List.of("Architect and manage data warehouse solutions in the cloud.", "Implement ETL/ELT processes using modern data integration tools.", "Perform complex SQL queries to extract insights from large datasets.", "Create comprehensive dashboards and visualizations for stakeholders."),
            List.of("Kafka", "BigQuery", "Hadoop", "R", "PostgreSQL", "Snowflake", "Apache Spark", "Python", "dbt", "Tableau"),
            List.of("How many years of experience do you have with BigQuery?"),
            58
        ),
        new SeedJobDefinition(
            "Data Engineer",
            "You will be responsible for creating intuitive dashboards and reporting solutions. Turn raw data into actionable insights to drive business strategy. Design and maintain scalable data pipelines and data warehouse infrastructure. Join us to make a significant impact on our industry.",
            "Paris, France",
            INTERNSHIP,
            HYBRID,
            List.of("Implement ETL/ELT processes using modern data integration tools.", "Create comprehensive dashboards and visualizations for stakeholders.", "Ensure data integrity, security, and compliance across all systems.", "Perform complex SQL queries to extract insights from large datasets.", "Design, build, and maintain scalable automated data pipelines.", "Collaborate with data scientists to prepare data for ML models."),
            List.of("Snowflake", "BigQuery", "Power BI", "PostgreSQL", "Airflow", "Data Modeling", "SQL", "Python", "dbt", "Hadoop"),
            List.of("How many years of experience do you have with BigQuery?", "What are your salary expectations?"),
            60
        ),
        new SeedJobDefinition(
            "Data Engineer",
            "Work closely with stakeholders to understand their data needs and deliver effective analytics. Design and maintain scalable data pipelines and data warehouse infrastructure. We are looking for a data expert to architect highly available database systems. We offer a collaborative and innovative work environment.",
            "Toronto, Canada",
            CONTRACT,
            REMOTE,
            List.of("Perform complex SQL queries to extract insights from large datasets.", "Architect and manage data warehouse solutions in the cloud.", "Create comprehensive dashboards and visualizations for stakeholders.", "Design, build, and maintain scalable automated data pipelines.", "Ensure data integrity, security, and compliance across all systems.", "Implement ETL/ELT processes using modern data integration tools.", "Optimize database performance and troubleshoot complex issues."),
            List.of("Redshift", "Kafka", "Power BI", "Apache Spark", "R", "NoSQL"),
            List.of("How many years of experience do you have with R?", "Describe a complex Data Engineer project you worked on."),
            80
        ),
        new SeedJobDefinition(
            "Data Engineer",
            "Turn raw data into actionable insights to drive business strategy. Join our data team to manage complex ETL processes and ensure data quality. Join us to make a significant impact on our industry.",
            "San Diego, CA",
            PART_TIME,
            ONSITE,
            List.of("Design, build, and maintain scalable automated data pipelines.", "Create comprehensive dashboards and visualizations for stakeholders.", "Architect and manage data warehouse solutions in the cloud.", "Perform complex SQL queries to extract insights from large datasets.", "Collaborate with data scientists to prepare data for ML models.", "Ensure data integrity, security, and compliance across all systems.", "Implement ETL/ELT processes using modern data integration tools."),
            List.of("PostgreSQL", "R", "Python", "Tableau", "dbt", "Redshift", "Snowflake", "Data Modeling"),
            List.of("How many years of experience do you have with R?", "Describe a complex Data Engineer project you worked on."),
            44
        ),
        new SeedJobDefinition(
            "BI Analyst",
            "Design and maintain scalable data pipelines and data warehouse infrastructure. Turn raw data into actionable insights to drive business strategy. You will be a key player in our growing, dynamic team.",
            "Visakhapatnam, India",
            FULL_TIME,
            REMOTE,
            List.of("Optimize database performance and troubleshoot complex issues.", "Collaborate with data scientists to prepare data for ML models.", "Ensure data integrity, security, and compliance across all systems.", "Perform complex SQL queries to extract insights from large datasets.", "Create comprehensive dashboards and visualizations for stakeholders."),
            List.of("Power BI", "Hadoop", "SQL", "NoSQL", "dbt", "Redshift", "Airflow", "Snowflake", "Apache Spark", "Data Modeling"),
            List.of("How many years of experience do you have with Power BI?", "What are your salary expectations?", "Describe a complex BI Analyst project you worked on."),
            51
        ),
        new SeedJobDefinition(
            "BI Analyst",
            "Design and maintain scalable data pipelines and data warehouse infrastructure. We are looking for a data expert to architect highly available database systems. You will be a key player in our growing, dynamic team.",
            "Los Angeles, CA",
            FULL_TIME,
            ONSITE,
            List.of("Ensure data integrity, security, and compliance across all systems.", "Collaborate with data scientists to prepare data for ML models.", "Architect and manage data warehouse solutions in the cloud.", "Design, build, and maintain scalable automated data pipelines.", "Create comprehensive dashboards and visualizations for stakeholders.", "Optimize database performance and troubleshoot complex issues.", "Implement ETL/ELT processes using modern data integration tools."),
            List.of("Python", "Tableau", "Airflow", "SQL", "dbt", "NoSQL", "Data Modeling", "BigQuery", "Kafka", "R"),
            List.of("How many years of experience do you have with Data Modeling?", "What are your salary expectations?"),
            66
        ),
        new SeedJobDefinition(
            "Analytics Engineer",
            "Turn raw data into actionable insights to drive business strategy. Work closely with stakeholders to understand their data needs and deliver effective analytics. You will be responsible for creating intuitive dashboards and reporting solutions. You will be a key player in our growing, dynamic team.",
            "Abu Dhabi, UAE",
            FULL_TIME,
            HYBRID,
            List.of("Create comprehensive dashboards and visualizations for stakeholders.", "Architect and manage data warehouse solutions in the cloud.", "Optimize database performance and troubleshoot complex issues.", "Implement ETL/ELT processes using modern data integration tools.", "Ensure data integrity, security, and compliance across all systems."),
            List.of("Kafka", "Power BI", "PostgreSQL", "NoSQL", "R", "BigQuery", "Airflow"),
            List.of("How many years of experience do you have with NoSQL?", "What are your salary expectations?"),
            15
        ),
        new SeedJobDefinition(
            "Analytics Engineer",
            "Join our data team to manage complex ETL processes and ensure data quality. Work closely with stakeholders to understand their data needs and deliver effective analytics. Design and maintain scalable data pipelines and data warehouse infrastructure. We offer a collaborative and innovative work environment.",
            "San Francisco, CA",
            FULL_TIME,
            ONSITE,
            List.of("Optimize database performance and troubleshoot complex issues.", "Design, build, and maintain scalable automated data pipelines.", "Implement ETL/ELT processes using modern data integration tools.", "Architect and manage data warehouse solutions in the cloud.", "Ensure data integrity, security, and compliance across all systems.", "Perform complex SQL queries to extract insights from large datasets.", "Create comprehensive dashboards and visualizations for stakeholders."),
            List.of("R", "Snowflake", "Power BI", "Tableau", "BigQuery", "Kafka", "SQL", "NoSQL", "Hadoop"),
            List.of("How many years of experience do you have with Snowflake?", "What are your salary expectations?", "Describe a complex Analytics Engineer project you worked on."),
            58
        ),
        new SeedJobDefinition(
            "Database Administrator",
            "Design and maintain scalable data pipelines and data warehouse infrastructure. We are looking for a data expert to architect highly available database systems. You will be responsible for creating intuitive dashboards and reporting solutions. Join us to make a significant impact on our industry.",
            "Toronto, Canada",
            CONTRACT,
            ONSITE,
            List.of("Design, build, and maintain scalable automated data pipelines.", "Ensure data integrity, security, and compliance across all systems.", "Collaborate with data scientists to prepare data for ML models.", "Architect and manage data warehouse solutions in the cloud.", "Implement ETL/ELT processes using modern data integration tools.", "Optimize database performance and troubleshoot complex issues.", "Create comprehensive dashboards and visualizations for stakeholders."),
            List.of("Kafka", "dbt", "R", "Snowflake", "PostgreSQL"),
            List.of("How many years of experience do you have with Kafka?", "Describe a complex Database Administrator project you worked on."),
            83
        ),
        new SeedJobDefinition(
            "Database Administrator",
            "We are looking for a data expert to architect highly available database systems. Work closely with stakeholders to understand their data needs and deliver effective analytics. Turn raw data into actionable insights to drive business strategy. We offer a collaborative and innovative work environment.",
            "Denver, CO",
            FULL_TIME,
            HYBRID,
            List.of("Collaborate with data scientists to prepare data for ML models.", "Ensure data integrity, security, and compliance across all systems.", "Create comprehensive dashboards and visualizations for stakeholders.", "Architect and manage data warehouse solutions in the cloud."),
            List.of("SQL", "dbt", "Python", "PostgreSQL", "Data Modeling", "R", "Tableau"),
            List.of("How many years of experience do you have with Data Modeling?", "Describe a complex Database Administrator project you worked on."),
            64
        ),
        new SeedJobDefinition(
            "Data Architect",
            "Design and maintain scalable data pipelines and data warehouse infrastructure. Turn raw data into actionable insights to drive business strategy. You will be responsible for creating intuitive dashboards and reporting solutions. You will be a key player in our growing, dynamic team.",
            "New York, NY",
            FULL_TIME,
            HYBRID,
            List.of("Create comprehensive dashboards and visualizations for stakeholders.", "Optimize database performance and troubleshoot complex issues.", "Collaborate with data scientists to prepare data for ML models.", "Ensure data integrity, security, and compliance across all systems.", "Architect and manage data warehouse solutions in the cloud."),
            List.of("Airflow", "Hadoop", "SQL", "dbt", "NoSQL", "PostgreSQL", "Data Modeling", "Python"),
            List.of("How many years of experience do you have with NoSQL?", "What are your salary expectations?"),
            49
        ),
        new SeedJobDefinition(
            "Data Architect",
            "Join our data team to manage complex ETL processes and ensure data quality. You will be responsible for creating intuitive dashboards and reporting solutions. You will be a key player in our growing, dynamic team.",
            "Delhi, India",
            FULL_TIME,
            REMOTE,
            List.of("Architect and manage data warehouse solutions in the cloud.", "Ensure data integrity, security, and compliance across all systems.", "Collaborate with data scientists to prepare data for ML models.", "Perform complex SQL queries to extract insights from large datasets.", "Design, build, and maintain scalable automated data pipelines."),
            List.of("Airflow", "dbt", "Hadoop", "Power BI", "Apache Spark"),
            List.of("How many years of experience do you have with dbt?", "What are your salary expectations?"),
            17
        ),
        new SeedJobDefinition(
            "ETL Developer",
            "Join our data team to manage complex ETL processes and ensure data quality. Work closely with stakeholders to understand their data needs and deliver effective analytics. We offer a collaborative and innovative work environment.",
            "Toronto, Canada",
            FULL_TIME,
            HYBRID,
            List.of("Ensure data integrity, security, and compliance across all systems.", "Design, build, and maintain scalable automated data pipelines.", "Optimize database performance and troubleshoot complex issues.", "Create comprehensive dashboards and visualizations for stakeholders.", "Architect and manage data warehouse solutions in the cloud.", "Collaborate with data scientists to prepare data for ML models."),
            List.of("PostgreSQL", "SQL", "Kafka", "Snowflake", "dbt", "R", "Tableau", "Data Modeling"),
            List.of("How many years of experience do you have with Tableau?"),
            30
        ),
        new SeedJobDefinition(
            "Senior Data Engineer",
            "We are looking for a data expert to architect highly available database systems. Design and maintain scalable data pipelines and data warehouse infrastructure. Join us to make a significant impact on our industry.",
            "Austin, TX",
            FULL_TIME,
            ONSITE,
            List.of("Perform complex SQL queries to extract insights from large datasets.", "Ensure data integrity, security, and compliance across all systems.", "Design, build, and maintain scalable automated data pipelines.", "Optimize database performance and troubleshoot complex issues."),
            List.of("SQL", "Apache Spark", "dbt", "Power BI", "Tableau", "Python", "BigQuery", "NoSQL", "Redshift"),
            List.of("How many years of experience do you have with SQL?"),
            22
        ),
        new SeedJobDefinition(
            "Lead Data Analyst",
            "We are looking for a data expert to architect highly available database systems. You will be responsible for creating intuitive dashboards and reporting solutions. Turn raw data into actionable insights to drive business strategy. We offer a collaborative and innovative work environment.",
            "Toronto, Canada",
            FULL_TIME,
            ONSITE,
            List.of("Design, build, and maintain scalable automated data pipelines.", "Collaborate with data scientists to prepare data for ML models.", "Ensure data integrity, security, and compliance across all systems.", "Perform complex SQL queries to extract insights from large datasets.", "Implement ETL/ELT processes using modern data integration tools.", "Optimize database performance and troubleshoot complex issues."),
            List.of("Snowflake", "Tableau", "BigQuery", "SQL", "Apache Spark"),
            List.of("How many years of experience do you have with Snowflake?", "What are your salary expectations?", "Describe a complex Lead Data Analyst project you worked on."),
            78
        ),
        new SeedJobDefinition(
            "Data Governance Specialist",
            "Design and maintain scalable data pipelines and data warehouse infrastructure. Turn raw data into actionable insights to drive business strategy. We are looking for a data expert to architect highly available database systems. We offer a collaborative and innovative work environment.",
            "Kochi, India",
            INTERNSHIP,
            HYBRID,
            List.of("Design, build, and maintain scalable automated data pipelines.", "Collaborate with data scientists to prepare data for ML models.", "Implement ETL/ELT processes using modern data integration tools.", "Perform complex SQL queries to extract insights from large datasets.", "Ensure data integrity, security, and compliance across all systems."),
            List.of("PostgreSQL", "Snowflake", "SQL", "NoSQL", "Kafka"),
            List.of("How many years of experience do you have with NoSQL?", "What are your salary expectations?"),
            20
        )
        );
    }

    /** Returns jobs for cloudDevOpsJobs */
    public static List<SeedJobDefinition> cloudDevOpsJobs() {
        return List.of(
        new SeedJobDefinition(
            "DevOps Engineer",
            "Join our SRE team to ensure the reliability and uptime of our critical systems. Help us build a robust internal developer platform to accelerate engineering delivery. The ideal candidate thrives in a fast-paced setting.",
            "Sydney, Australia",
            INTERNSHIP,
            REMOTE,
            List.of("Collaborate with development teams to optimize application performance.", "Implement security best practices and compliance controls in the cloud.", "Automate repetitive operational tasks to improve team efficiency.", "Monitor system performance and troubleshoot production incidents.", "Manage and scale Kubernetes clusters across multiple environments."),
            List.of("Azure", "AWS", "Ansible", "Prometheus", "Bash", "GCP", "GitLab CI", "Nginx", "Python", "Docker"),
            List.of("How many years of experience do you have with Python?", "What are your salary expectations?", "Describe a complex DevOps Engineer project you worked on."),
            28
        ),
        new SeedJobDefinition(
            "DevOps Engineer",
            "You will manage and improve our CI/CD pipelines and deployment processes. Drive the automation and scalability of our cloud infrastructure. Take charge of our Kubernetes clusters and container orchestration strategy. You will be a key player in our growing, dynamic team.",
            "Noida, India",
            PART_TIME,
            HYBRID,
            List.of("Collaborate with development teams to optimize application performance.", "Implement security best practices and compliance controls in the cloud.", "Design and implement secure cloud infrastructure using infrastructure-as-code.", "Monitor system performance and troubleshoot production incidents.", "Build and maintain robust CI/CD pipelines for automated deployments.", "Manage and scale Kubernetes clusters across multiple environments."),
            List.of("Kubernetes", "Ansible", "Python", "AWS", "Azure", "Terraform"),
            List.of("How many years of experience do you have with Kubernetes?"),
            71
        ),
        new SeedJobDefinition(
            "DevOps Engineer",
            "Help us build a robust internal developer platform to accelerate engineering delivery. Drive the automation and scalability of our cloud infrastructure. Take charge of our Kubernetes clusters and container orchestration strategy. We offer a collaborative and innovative work environment.",
            "Abu Dhabi, UAE",
            FULL_TIME,
            ONSITE,
            List.of("Build and maintain robust CI/CD pipelines for automated deployments.", "Monitor system performance and troubleshoot production incidents.", "Automate repetitive operational tasks to improve team efficiency.", "Perform capacity planning and disaster recovery testing.", "Manage and scale Kubernetes clusters across multiple environments.", "Collaborate with development teams to optimize application performance.", "Implement security best practices and compliance controls in the cloud."),
            List.of("Grafana", "GCP", "Nginx", "Bash", "Datadog", "AWS", "Terraform", "Ansible", "GitLab CI"),
            List.of("How many years of experience do you have with Nginx?"),
            11
        ),
        new SeedJobDefinition(
            "Cloud Engineer",
            "Join our SRE team to ensure the reliability and uptime of our critical systems. You will manage and improve our CI/CD pipelines and deployment processes. Help us build a robust internal developer platform to accelerate engineering delivery. You will be a key player in our growing, dynamic team.",
            "Toronto, Canada",
            INTERNSHIP,
            ONSITE,
            List.of("Automate repetitive operational tasks to improve team efficiency.", "Manage and scale Kubernetes clusters across multiple environments.", "Perform capacity planning and disaster recovery testing.", "Build and maintain robust CI/CD pipelines for automated deployments.", "Implement security best practices and compliance controls in the cloud.", "Collaborate with development teams to optimize application performance."),
            List.of("Datadog", "GitLab CI", "Terraform", "AWS", "Kubernetes", "GCP", "Python", "Prometheus", "Azure"),
            List.of("How many years of experience do you have with GitLab CI?", "What are your salary expectations?"),
            60
        ),
        new SeedJobDefinition(
            "Cloud Engineer",
            "Help us build a robust internal developer platform to accelerate engineering delivery. You will manage and improve our CI/CD pipelines and deployment processes. Join our SRE team to ensure the reliability and uptime of our critical systems. The ideal candidate thrives in a fast-paced setting.",
            "Hyderabad, India",
            CONTRACT,
            HYBRID,
            List.of("Design and implement secure cloud infrastructure using infrastructure-as-code.", "Implement security best practices and compliance controls in the cloud.", "Manage and scale Kubernetes clusters across multiple environments.", "Automate repetitive operational tasks to improve team efficiency.", "Build and maintain robust CI/CD pipelines for automated deployments.", "Perform capacity planning and disaster recovery testing."),
            List.of("Jenkins", "Python", "Prometheus", "Nginx", "Terraform", "GCP", "Kubernetes", "GitLab CI", "Azure", "Docker"),
            List.of("How many years of experience do you have with Terraform?"),
            49
        ),
        new SeedJobDefinition(
            "AWS Solutions Architect",
            "We are looking for a cloud expert to design highly available and secure architectures. Help us build a robust internal developer platform to accelerate engineering delivery. You will manage and improve our CI/CD pipelines and deployment processes. Join us to make a significant impact on our industry.",
            "Atlanta, GA",
            FULL_TIME,
            ONSITE,
            List.of("Automate repetitive operational tasks to improve team efficiency.", "Design and implement secure cloud infrastructure using infrastructure-as-code.", "Manage and scale Kubernetes clusters across multiple environments.", "Perform capacity planning and disaster recovery testing.", "Collaborate with development teams to optimize application performance."),
            List.of("Kubernetes", "Terraform", "Azure", "GCP", "Prometheus", "Bash", "Datadog", "Docker", "Grafana"),
            List.of("How many years of experience do you have with Prometheus?"),
            73
        ),
        new SeedJobDefinition(
            "Azure Cloud Engineer",
            "Join our SRE team to ensure the reliability and uptime of our critical systems. Help us build a robust internal developer platform to accelerate engineering delivery. Take charge of our Kubernetes clusters and container orchestration strategy. The ideal candidate thrives in a fast-paced setting.",
            "Mumbai, India",
            FULL_TIME,
            REMOTE,
            List.of("Perform capacity planning and disaster recovery testing.", "Implement security best practices and compliance controls in the cloud.", "Manage and scale Kubernetes clusters across multiple environments.", "Collaborate with development teams to optimize application performance.", "Monitor system performance and troubleshoot production incidents."),
            List.of("Datadog", "Docker", "Python", "Grafana", "Jenkins", "Ansible", "Nginx", "AWS", "Linux", "Bash"),
            List.of("How many years of experience do you have with AWS?"),
            33
        ),
        new SeedJobDefinition(
            "Cloud Architect",
            "We are looking for a cloud expert to design highly available and secure architectures. Help us build a robust internal developer platform to accelerate engineering delivery. The ideal candidate thrives in a fast-paced setting.",
            "Seattle, WA",
            CONTRACT,
            HYBRID,
            List.of("Design and implement secure cloud infrastructure using infrastructure-as-code.", "Perform capacity planning and disaster recovery testing.", "Collaborate with development teams to optimize application performance.", "Implement security best practices and compliance controls in the cloud.", "Build and maintain robust CI/CD pipelines for automated deployments.", "Manage and scale Kubernetes clusters across multiple environments.", "Automate repetitive operational tasks to improve team efficiency."),
            List.of("Bash", "GCP", "Docker", "Prometheus", "Terraform"),
            List.of("How many years of experience do you have with Terraform?", "What are your salary expectations?"),
            35
        ),
        new SeedJobDefinition(
            "Cloud Architect",
            "Drive the automation and scalability of our cloud infrastructure. You will manage and improve our CI/CD pipelines and deployment processes. Join us to make a significant impact on our industry.",
            "Atlanta, GA",
            FULL_TIME,
            HYBRID,
            List.of("Monitor system performance and troubleshoot production incidents.", "Collaborate with development teams to optimize application performance.", "Implement security best practices and compliance controls in the cloud.", "Automate repetitive operational tasks to improve team efficiency."),
            List.of("Ansible", "Bash", "Linux", "Nginx", "Python", "Prometheus", "AWS"),
            List.of("How many years of experience do you have with Linux?"),
            23
        ),
        new SeedJobDefinition(
            "Site Reliability Engineer",
            "Join our SRE team to ensure the reliability and uptime of our critical systems. You will manage and improve our CI/CD pipelines and deployment processes. You will be a key player in our growing, dynamic team.",
            "Dublin, Ireland",
            FULL_TIME,
            HYBRID,
            List.of("Manage and scale Kubernetes clusters across multiple environments.", "Build and maintain robust CI/CD pipelines for automated deployments.", "Collaborate with development teams to optimize application performance.", "Design and implement secure cloud infrastructure using infrastructure-as-code.", "Perform capacity planning and disaster recovery testing.", "Monitor system performance and troubleshoot production incidents.", "Implement security best practices and compliance controls in the cloud."),
            List.of("Python", "Prometheus", "Grafana", "Linux", "Datadog", "AWS", "Azure", "Jenkins"),
            List.of("How many years of experience do you have with AWS?", "What are your salary expectations?"),
            42
        ),
        new SeedJobDefinition(
            "Site Reliability Engineer",
            "We are looking for a cloud expert to design highly available and secure architectures. Take charge of our Kubernetes clusters and container orchestration strategy. You will manage and improve our CI/CD pipelines and deployment processes. Join us to make a significant impact on our industry.",
            "New York, NY",
            FULL_TIME,
            HYBRID,
            List.of("Automate repetitive operational tasks to improve team efficiency.", "Design and implement secure cloud infrastructure using infrastructure-as-code.", "Manage and scale Kubernetes clusters across multiple environments.", "Build and maintain robust CI/CD pipelines for automated deployments.", "Implement security best practices and compliance controls in the cloud.", "Monitor system performance and troubleshoot production incidents.", "Perform capacity planning and disaster recovery testing."),
            List.of("Terraform", "Kubernetes", "GitLab CI", "Python", "Docker", "Ansible", "Prometheus"),
            List.of("How many years of experience do you have with GitLab CI?", "Describe a complex Site Reliability Engineer project you worked on."),
            15
        ),
        new SeedJobDefinition(
            "Platform Engineer",
            "We are looking for a cloud expert to design highly available and secure architectures. You will manage and improve our CI/CD pipelines and deployment processes. Drive the automation and scalability of our cloud infrastructure. You will be a key player in our growing, dynamic team.",
            "Paris, France",
            PART_TIME,
            REMOTE,
            List.of("Perform capacity planning and disaster recovery testing.", "Collaborate with development teams to optimize application performance.", "Implement security best practices and compliance controls in the cloud.", "Manage and scale Kubernetes clusters across multiple environments.", "Build and maintain robust CI/CD pipelines for automated deployments.", "Monitor system performance and troubleshoot production incidents.", "Automate repetitive operational tasks to improve team efficiency."),
            List.of("Grafana", "Bash", "Jenkins", "Python", "Kubernetes", "Datadog", "Prometheus", "AWS", "Ansible"),
            List.of("How many years of experience do you have with Bash?", "Describe a complex Platform Engineer project you worked on."),
            44
        ),
        new SeedJobDefinition(
            "Platform Engineer",
            "Join our SRE team to ensure the reliability and uptime of our critical systems. Take charge of our Kubernetes clusters and container orchestration strategy. The ideal candidate thrives in a fast-paced setting.",
            "Toronto, Canada",
            FULL_TIME,
            HYBRID,
            List.of("Perform capacity planning and disaster recovery testing.", "Implement security best practices and compliance controls in the cloud.", "Design and implement secure cloud infrastructure using infrastructure-as-code.", "Build and maintain robust CI/CD pipelines for automated deployments.", "Automate repetitive operational tasks to improve team efficiency.", "Collaborate with development teams to optimize application performance."),
            List.of("GCP", "Datadog", "Python", "Nginx", "Kubernetes", "Prometheus", "Jenkins", "Ansible", "Azure"),
            List.of("How many years of experience do you have with Prometheus?", "What are your salary expectations?"),
            33
        ),
        new SeedJobDefinition(
            "Kubernetes Engineer",
            "We are looking for a cloud expert to design highly available and secure architectures. Take charge of our Kubernetes clusters and container orchestration strategy. You will manage and improve our CI/CD pipelines and deployment processes. We offer a collaborative and innovative work environment.",
            "Los Angeles, CA",
            FULL_TIME,
            HYBRID,
            List.of("Collaborate with development teams to optimize application performance.", "Implement security best practices and compliance controls in the cloud.", "Perform capacity planning and disaster recovery testing.", "Monitor system performance and troubleshoot production incidents.", "Manage and scale Kubernetes clusters across multiple environments.", "Automate repetitive operational tasks to improve team efficiency.", "Build and maintain robust CI/CD pipelines for automated deployments."),
            List.of("Bash", "Grafana", "GCP", "GitLab CI", "AWS", "Nginx"),
            List.of("How many years of experience do you have with AWS?", "What are your salary expectations?"),
            51
        ),
        new SeedJobDefinition(
            "Infrastructure Engineer",
            "Take charge of our Kubernetes clusters and container orchestration strategy. We are looking for a cloud expert to design highly available and secure architectures. You will be a key player in our growing, dynamic team.",
            "Ahmedabad, India",
            FULL_TIME,
            REMOTE,
            List.of("Perform capacity planning and disaster recovery testing.", "Build and maintain robust CI/CD pipelines for automated deployments.", "Monitor system performance and troubleshoot production incidents.", "Automate repetitive operational tasks to improve team efficiency.", "Collaborate with development teams to optimize application performance.", "Design and implement secure cloud infrastructure using infrastructure-as-code.", "Implement security best practices and compliance controls in the cloud."),
            List.of("Docker", "Datadog", "Python", "Nginx", "Terraform", "Ansible", "Kubernetes", "GitLab CI", "Linux"),
            List.of("How many years of experience do you have with GitLab CI?", "Describe a complex Infrastructure Engineer project you worked on."),
            35
        ),
        new SeedJobDefinition(
            "DevSecOps Engineer",
            "Drive the automation and scalability of our cloud infrastructure. You will manage and improve our CI/CD pipelines and deployment processes. We are looking for a cloud expert to design highly available and secure architectures. Join us to make a significant impact on our industry.",
            "Visakhapatnam, India",
            FULL_TIME,
            HYBRID,
            List.of("Implement security best practices and compliance controls in the cloud.", "Collaborate with development teams to optimize application performance.", "Perform capacity planning and disaster recovery testing.", "Design and implement secure cloud infrastructure using infrastructure-as-code.", "Monitor system performance and troubleshoot production incidents.", "Manage and scale Kubernetes clusters across multiple environments."),
            List.of("Terraform", "Bash", "Linux", "Nginx", "GCP"),
            List.of("How many years of experience do you have with Linux?", "What are your salary expectations?", "Describe a complex DevSecOps Engineer project you worked on."),
            2
        ),
        new SeedJobDefinition(
            "Cloud Operations Manager",
            "You will manage and improve our CI/CD pipelines and deployment processes. Help us build a robust internal developer platform to accelerate engineering delivery. You will be a key player in our growing, dynamic team.",
            "Kochi, India",
            PART_TIME,
            ONSITE,
            List.of("Design and implement secure cloud infrastructure using infrastructure-as-code.", "Automate repetitive operational tasks to improve team efficiency.", "Perform capacity planning and disaster recovery testing.", "Build and maintain robust CI/CD pipelines for automated deployments.", "Monitor system performance and troubleshoot production incidents.", "Implement security best practices and compliance controls in the cloud.", "Collaborate with development teams to optimize application performance."),
            List.of("Prometheus", "GitLab CI", "Kubernetes", "Nginx", "Python", "AWS", "Azure", "Linux", "Datadog", "Jenkins"),
            List.of("How many years of experience do you have with Python?"),
            86
        )
        );
    }

    /** Returns jobs for cybersecurityJobs */
    public static List<SeedJobDefinition> cybersecurityJobs() {
        return List.of(
        new SeedJobDefinition(
            "Cybersecurity Analyst",
            "Join our security team to integrate security practices into the software development lifecycle. Protect our infrastructure and data from emerging cyber threats. We are looking for an expert to design and implement secure cloud architectures. The ideal candidate thrives in a fast-paced setting.",
            "Paris, France",
            PART_TIME,
            ONSITE,
            List.of("Develop and maintain information security policies and procedures.", "Conduct regular vulnerability assessments and penetration testing.", "Audit cloud infrastructure and enforce security configurations.", "Provide security training and guidance to engineering teams."),
            List.of("SIEM", "Wireshark", "Bash", "Firewalls", "Cryptography", "OWASP"),
            List.of("How many years of experience do you have with OWASP?", "What are your salary expectations?"),
            32
        ),
        new SeedJobDefinition(
            "Cybersecurity Analyst",
            "Lead the development of our enterprise security strategy and compliance programs. Protect our infrastructure and data from emerging cyber threats. We are looking for an expert to design and implement secure cloud architectures. Join us to make a significant impact on our industry.",
            "Noida, India",
            CONTRACT,
            ONSITE,
            List.of("Audit cloud infrastructure and enforce security configurations.", "Monitor network traffic for security events and analyze alerts.", "Develop and maintain information security policies and procedures.", "Design and implement zero-trust architectures and IAM policies."),
            List.of("Network Security", "Cloud Security", "Python", "Firewalls", "Burp Suite"),
            List.of("How many years of experience do you have with Python?"),
            29
        ),
        new SeedJobDefinition(
            "Security Engineer",
            "We are looking for an expert to design and implement secure cloud architectures. You will monitor our networks for security breaches and investigate incidents. Protect our infrastructure and data from emerging cyber threats. You will be a key player in our growing, dynamic team.",
            "Abu Dhabi, UAE",
            FULL_TIME,
            HYBRID,
            List.of("Conduct regular vulnerability assessments and penetration testing.", "Audit cloud infrastructure and enforce security configurations.", "Respond to security incidents and perform forensic analysis.", "Integrate security tools and processes into the CI/CD pipeline.", "Design and implement zero-trust architectures and IAM policies.", "Monitor network traffic for security events and analyze alerts.", "Develop and maintain information security policies and procedures."),
            List.of("Penetration Testing", "IAM", "Incident Response", "Cryptography", "SIEM", "Wireshark", "Python", "Burp Suite"),
            List.of("How many years of experience do you have with Penetration Testing?", "What are your salary expectations?"),
            25
        ),
        new SeedJobDefinition(
            "Security Engineer",
            "Join our security team to integrate security practices into the software development lifecycle. Lead the development of our enterprise security strategy and compliance programs. The ideal candidate thrives in a fast-paced setting.",
            "Sydney, Australia",
            FULL_TIME,
            REMOTE,
            List.of("Audit cloud infrastructure and enforce security configurations.", "Provide security training and guidance to engineering teams.", "Design and implement zero-trust architectures and IAM policies.", "Integrate security tools and processes into the CI/CD pipeline.", "Monitor network traffic for security events and analyze alerts."),
            List.of("OWASP", "Python", "Cryptography", "SIEM", "Bash", "Network Security", "Burp Suite", "IAM", "Cloud Security"),
            List.of("How many years of experience do you have with Network Security?", "What are your salary expectations?"),
            77
        ),
        new SeedJobDefinition(
            "SOC Analyst",
            "You will monitor our networks for security breaches and investigate incidents. Protect our infrastructure and data from emerging cyber threats. We are looking for an expert to design and implement secure cloud architectures. The ideal candidate thrives in a fast-paced setting.",
            "Austin, TX",
            INTERNSHIP,
            ONSITE,
            List.of("Conduct regular vulnerability assessments and penetration testing.", "Integrate security tools and processes into the CI/CD pipeline.", "Audit cloud infrastructure and enforce security configurations.", "Monitor network traffic for security events and analyze alerts."),
            List.of("IAM", "OWASP", "Burp Suite", "Network Security", "Splunk", "SIEM"),
            List.of("How many years of experience do you have with Network Security?"),
            19
        ),
        new SeedJobDefinition(
            "SOC Analyst",
            "You will monitor our networks for security breaches and investigate incidents. Protect our infrastructure and data from emerging cyber threats. Join our security team to integrate security practices into the software development lifecycle. Join us to make a significant impact on our industry.",
            "San Francisco, CA",
            FULL_TIME,
            HYBRID,
            List.of("Provide security training and guidance to engineering teams.", "Design and implement zero-trust architectures and IAM policies.", "Monitor network traffic for security events and analyze alerts.", "Audit cloud infrastructure and enforce security configurations.", "Conduct regular vulnerability assessments and penetration testing."),
            List.of("IAM", "SIEM", "Python", "Bash", "Burp Suite", "Network Security", "Cloud Security", "OWASP"),
            List.of("How many years of experience do you have with Network Security?", "What are your salary expectations?"),
            63
        ),
        new SeedJobDefinition(
            "Application Security Engineer",
            "Join our security team to integrate security practices into the software development lifecycle. You will monitor our networks for security breaches and investigate incidents. You will be a key player in our growing, dynamic team.",
            "Abu Dhabi, UAE",
            INTERNSHIP,
            HYBRID,
            List.of("Audit cloud infrastructure and enforce security configurations.", "Monitor network traffic for security events and analyze alerts.", "Develop and maintain information security policies and procedures.", "Respond to security incidents and perform forensic analysis."),
            List.of("IAM", "OWASP", "Penetration Testing", "Firewalls", "Splunk", "Network Security", "Burp Suite"),
            List.of("How many years of experience do you have with Firewalls?", "Describe a complex Application Security Engineer project you worked on."),
            20
        ),
        new SeedJobDefinition(
            "Application Security Engineer",
            "You will monitor our networks for security breaches and investigate incidents. Conduct penetration testing and vulnerability assessments on our applications. Join us to make a significant impact on our industry.",
            "Bengaluru, India",
            INTERNSHIP,
            REMOTE,
            List.of("Audit cloud infrastructure and enforce security configurations.", "Develop and maintain information security policies and procedures.", "Respond to security incidents and perform forensic analysis.", "Provide security training and guidance to engineering teams.", "Design and implement zero-trust architectures and IAM policies."),
            List.of("IAM", "Cloud Security", "Network Security", "OWASP", "Firewalls", "Splunk"),
            List.of("How many years of experience do you have with Firewalls?"),
            35
        ),
        new SeedJobDefinition(
            "Cloud Security Engineer",
            "Conduct penetration testing and vulnerability assessments on our applications. We are looking for an expert to design and implement secure cloud architectures. Join our security team to integrate security practices into the software development lifecycle. You will be a key player in our growing, dynamic team.",
            "Gurugram, India",
            FULL_TIME,
            HYBRID,
            List.of("Provide security training and guidance to engineering teams.", "Respond to security incidents and perform forensic analysis.", "Design and implement zero-trust architectures and IAM policies.", "Develop and maintain information security policies and procedures.", "Audit cloud infrastructure and enforce security configurations."),
            List.of("Cloud Security", "Wireshark", "Network Security", "Penetration Testing", "SIEM", "Splunk", "Incident Response"),
            List.of("How many years of experience do you have with Splunk?", "What are your salary expectations?"),
            46
        ),
        new SeedJobDefinition(
            "Security Architect",
            "Protect our infrastructure and data from emerging cyber threats. You will monitor our networks for security breaches and investigate incidents. Join our security team to integrate security practices into the software development lifecycle. Join us to make a significant impact on our industry.",
            "Munich, Germany",
            FULL_TIME,
            ONSITE,
            List.of("Integrate security tools and processes into the CI/CD pipeline.", "Conduct regular vulnerability assessments and penetration testing.", "Audit cloud infrastructure and enforce security configurations.", "Provide security training and guidance to engineering teams.", "Monitor network traffic for security events and analyze alerts.", "Develop and maintain information security policies and procedures.", "Respond to security incidents and perform forensic analysis."),
            List.of("Firewalls", "Burp Suite", "Network Security", "Incident Response", "IAM", "Cloud Security", "Python", "Splunk"),
            List.of("How many years of experience do you have with Python?", "Describe a complex Security Architect project you worked on."),
            68
        ),
        new SeedJobDefinition(
            "Security Architect",
            "You will monitor our networks for security breaches and investigate incidents. We are looking for an expert to design and implement secure cloud architectures. Protect our infrastructure and data from emerging cyber threats. The ideal candidate thrives in a fast-paced setting.",
            "Singapore",
            PART_TIME,
            REMOTE,
            List.of("Design and implement zero-trust architectures and IAM policies.", "Monitor network traffic for security events and analyze alerts.", "Conduct regular vulnerability assessments and penetration testing.", "Provide security training and guidance to engineering teams."),
            List.of("IAM", "SIEM", "Firewalls", "Incident Response", "Burp Suite", "Bash", "Penetration Testing", "Wireshark", "Splunk", "Cryptography"),
            List.of("How many years of experience do you have with SIEM?", "Describe a complex Security Architect project you worked on."),
            85
        ),
        new SeedJobDefinition(
            "Penetration Tester",
            "Lead the development of our enterprise security strategy and compliance programs. We are looking for an expert to design and implement secure cloud architectures. The ideal candidate thrives in a fast-paced setting.",
            "Melbourne, Australia",
            FULL_TIME,
            ONSITE,
            List.of("Respond to security incidents and perform forensic analysis.", "Integrate security tools and processes into the CI/CD pipeline.", "Audit cloud infrastructure and enforce security configurations.", "Monitor network traffic for security events and analyze alerts.", "Conduct regular vulnerability assessments and penetration testing.", "Provide security training and guidance to engineering teams.", "Design and implement zero-trust architectures and IAM policies."),
            List.of("IAM", "Cloud Security", "Python", "Penetration Testing", "SIEM", "Incident Response", "Bash"),
            List.of("How many years of experience do you have with Bash?"),
            47
        ),
        new SeedJobDefinition(
            "Information Security Manager",
            "Lead the development of our enterprise security strategy and compliance programs. We are looking for an expert to design and implement secure cloud architectures. Join us to make a significant impact on our industry.",
            "Melbourne, Australia",
            FULL_TIME,
            REMOTE,
            List.of("Monitor network traffic for security events and analyze alerts.", "Provide security training and guidance to engineering teams.", "Conduct regular vulnerability assessments and penetration testing.", "Design and implement zero-trust architectures and IAM policies.", "Audit cloud infrastructure and enforce security configurations."),
            List.of("Burp Suite", "Firewalls", "Cryptography", "Bash", "IAM", "Network Security", "Splunk", "OWASP", "Penetration Testing"),
            List.of("How many years of experience do you have with Bash?", "What are your salary expectations?"),
            72
        ),
        new SeedJobDefinition(
            "Identity and Access Management Engineer",
            "Join our security team to integrate security practices into the software development lifecycle. Protect our infrastructure and data from emerging cyber threats. You will monitor our networks for security breaches and investigate incidents. We offer a collaborative and innovative work environment.",
            "Chicago, IL",
            FULL_TIME,
            REMOTE,
            List.of("Conduct regular vulnerability assessments and penetration testing.", "Monitor network traffic for security events and analyze alerts.", "Integrate security tools and processes into the CI/CD pipeline.", "Design and implement zero-trust architectures and IAM policies.", "Develop and maintain information security policies and procedures.", "Respond to security incidents and perform forensic analysis.", "Audit cloud infrastructure and enforce security configurations."),
            List.of("Cryptography", "Incident Response", "Wireshark", "SIEM", "OWASP", "Network Security", "IAM", "Cloud Security", "Splunk", "Burp Suite"),
            List.of("How many years of experience do you have with Network Security?", "What are your salary expectations?", "Describe a complex Identity and Access Management Engineer project you worked on."),
            79
        )
        );
    }

    /** Returns jobs for mobileJobs */
    public static List<SeedJobDefinition> mobileJobs() {
        return List.of(
        new SeedJobDefinition(
            "Android Developer",
            "You will lead the development of our flagship mobile app using modern frameworks. Build intuitive and performant mobile applications for millions of users. We need a mobile expert to optimize application performance and reduce load times. Join us to make a significant impact on our industry.",
            "Kolkata, India",
            CONTRACT,
            REMOTE,
            List.of("Ensure the best possible performance, quality, and responsiveness of the application.", "Design and build advanced applications for the iOS or Android platforms.", "Maintain code quality, organization, and automatization.", "Work on bug fixing and improving application performance."),
            List.of("iOS SDK", "Flutter", "Git", "Kotlin", "GraphQL", "Swift", "React Native", "JavaScript"),
            List.of("How many years of experience do you have with Kotlin?"),
            18
        ),
        new SeedJobDefinition(
            "Android Developer",
            "We need a mobile expert to optimize application performance and reduce load times. Drive the technical strategy for our mobile engineering initiatives. Join us to make a significant impact on our industry.",
            "Kochi, India",
            FULL_TIME,
            ONSITE,
            List.of("Continuously discover, evaluate, and implement new technologies to maximize development efficiency.", "Unit-test code for robustness, including edge cases, usability, and general reliability.", "Work on bug fixing and improving application performance.", "Collaborate with cross-functional teams to define, design, and ship new features.", "Ensure the best possible performance, quality, and responsiveness of the application."),
            List.of("TypeScript", "Git", "Objective-C", "Java", "GraphQL", "Flutter", "Dart"),
            List.of("How many years of experience do you have with TypeScript?", "What are your salary expectations?"),
            50
        ),
        new SeedJobDefinition(
            "iOS Developer",
            "We need a mobile expert to optimize application performance and reduce load times. Collaborate with designers to implement pixel-perfect user interfaces. The ideal candidate thrives in a fast-paced setting.",
            "Amsterdam, Netherlands",
            INTERNSHIP,
            REMOTE,
            List.of("Maintain code quality, organization, and automatization.", "Integrate with RESTful APIs and third-party libraries.", "Design and build advanced applications for the iOS or Android platforms.", "Collaborate with cross-functional teams to define, design, and ship new features.", "Continuously discover, evaluate, and implement new technologies to maximize development efficiency.", "Ensure the best possible performance, quality, and responsiveness of the application."),
            List.of("React Native", "Objective-C", "REST APIs", "Flutter", "TypeScript", "Kotlin", "Android SDK", "Git", "Java", "Swift"),
            List.of("How many years of experience do you have with REST APIs?", "What are your salary expectations?", "Describe a complex iOS Developer project you worked on."),
            35
        ),
        new SeedJobDefinition(
            "iOS Developer",
            "Drive the technical strategy for our mobile engineering initiatives. Collaborate with designers to implement pixel-perfect user interfaces. Join our cross-platform team to deliver seamless experiences on iOS and Android. Join us to make a significant impact on our industry.",
            "Mumbai, India",
            FULL_TIME,
            REMOTE,
            List.of("Integrate with RESTful APIs and third-party libraries.", "Maintain code quality, organization, and automatization.", "Work on bug fixing and improving application performance.", "Collaborate with cross-functional teams to define, design, and ship new features.", "Continuously discover, evaluate, and implement new technologies to maximize development efficiency.", "Unit-test code for robustness, including edge cases, usability, and general reliability."),
            List.of("Git", "JavaScript", "Java", "GraphQL", "Objective-C", "TypeScript"),
            List.of("How many years of experience do you have with TypeScript?"),
            75
        ),
        new SeedJobDefinition(
            "Flutter Developer",
            "Collaborate with designers to implement pixel-perfect user interfaces. We need a mobile expert to optimize application performance and reduce load times. You will be a key player in our growing, dynamic team.",
            "Amsterdam, Netherlands",
            CONTRACT,
            HYBRID,
            List.of("Work on bug fixing and improving application performance.", "Unit-test code for robustness, including edge cases, usability, and general reliability.", "Integrate with RESTful APIs and third-party libraries.", "Continuously discover, evaluate, and implement new technologies to maximize development efficiency.", "Ensure the best possible performance, quality, and responsiveness of the application.", "Maintain code quality, organization, and automatization."),
            List.of("Flutter", "Swift", "iOS SDK", "TypeScript", "Mobile UI", "GraphQL", "Dart", "REST APIs"),
            List.of("How many years of experience do you have with GraphQL?", "What are your salary expectations?"),
            17
        ),
        new SeedJobDefinition(
            "Flutter Developer",
            "Drive the technical strategy for our mobile engineering initiatives. You will lead the development of our flagship mobile app using modern frameworks. Collaborate with designers to implement pixel-perfect user interfaces. We offer a collaborative and innovative work environment.",
            "Dubai, UAE",
            CONTRACT,
            HYBRID,
            List.of("Maintain code quality, organization, and automatization.", "Unit-test code for robustness, including edge cases, usability, and general reliability.", "Integrate with RESTful APIs and third-party libraries.", "Ensure the best possible performance, quality, and responsiveness of the application."),
            List.of("React Native", "Android SDK", "Mobile UI", "Flutter", "TypeScript", "Objective-C"),
            List.of("How many years of experience do you have with Mobile UI?"),
            39
        ),
        new SeedJobDefinition(
            "React Native Developer",
            "You will lead the development of our flagship mobile app using modern frameworks. Build intuitive and performant mobile applications for millions of users. The ideal candidate thrives in a fast-paced setting.",
            "Bengaluru, India",
            CONTRACT,
            HYBRID,
            List.of("Integrate with RESTful APIs and third-party libraries.", "Continuously discover, evaluate, and implement new technologies to maximize development efficiency.", "Work on bug fixing and improving application performance.", "Design and build advanced applications for the iOS or Android platforms.", "Collaborate with cross-functional teams to define, design, and ship new features.", "Ensure the best possible performance, quality, and responsiveness of the application."),
            List.of("Mobile UI", "TypeScript", "Java", "iOS SDK", "REST APIs", "GraphQL", "JavaScript", "Kotlin", "Android SDK", "Flutter"),
            List.of("How many years of experience do you have with GraphQL?"),
            54
        ),
        new SeedJobDefinition(
            "React Native Developer",
            "Collaborate with designers to implement pixel-perfect user interfaces. You will lead the development of our flagship mobile app using modern frameworks. We offer a collaborative and innovative work environment.",
            "Dublin, Ireland",
            CONTRACT,
            ONSITE,
            List.of("Maintain code quality, organization, and automatization.", "Ensure the best possible performance, quality, and responsiveness of the application.", "Work on bug fixing and improving application performance.", "Design and build advanced applications for the iOS or Android platforms."),
            List.of("Flutter", "React Native", "JavaScript", "Java", "iOS SDK", "Objective-C", "Android SDK", "Kotlin", "Swift"),
            List.of("How many years of experience do you have with Kotlin?", "What are your salary expectations?"),
            88
        ),
        new SeedJobDefinition(
            "Mobile Software Engineer",
            "You will lead the development of our flagship mobile app using modern frameworks. Build intuitive and performant mobile applications for millions of users. Drive the technical strategy for our mobile engineering initiatives. You will be a key player in our growing, dynamic team.",
            "Kochi, India",
            FULL_TIME,
            REMOTE,
            List.of("Ensure the best possible performance, quality, and responsiveness of the application.", "Design and build advanced applications for the iOS or Android platforms.", "Collaborate with cross-functional teams to define, design, and ship new features.", "Unit-test code for robustness, including edge cases, usability, and general reliability.", "Maintain code quality, organization, and automatization.", "Continuously discover, evaluate, and implement new technologies to maximize development efficiency."),
            List.of("JavaScript", "Java", "Android SDK", "Flutter", "GraphQL", "iOS SDK"),
            List.of("How many years of experience do you have with Flutter?"),
            23
        ),
        new SeedJobDefinition(
            "Mobile Software Engineer",
            "Join our cross-platform team to deliver seamless experiences on iOS and Android. We need a mobile expert to optimize application performance and reduce load times. Drive the technical strategy for our mobile engineering initiatives. The ideal candidate thrives in a fast-paced setting.",
            "Seattle, WA",
            INTERNSHIP,
            HYBRID,
            List.of("Design and build advanced applications for the iOS or Android platforms.", "Continuously discover, evaluate, and implement new technologies to maximize development efficiency.", "Ensure the best possible performance, quality, and responsiveness of the application.", "Collaborate with cross-functional teams to define, design, and ship new features.", "Integrate with RESTful APIs and third-party libraries.", "Work on bug fixing and improving application performance.", "Unit-test code for robustness, including edge cases, usability, and general reliability."),
            List.of("iOS SDK", "Android SDK", "Dart", "JavaScript", "TypeScript", "Flutter", "GraphQL", "Git"),
            List.of("How many years of experience do you have with iOS SDK?", "What are your salary expectations?"),
            13
        ),
        new SeedJobDefinition(
            "Senior Mobile Engineer",
            "You will lead the development of our flagship mobile app using modern frameworks. Collaborate with designers to implement pixel-perfect user interfaces. Join our cross-platform team to deliver seamless experiences on iOS and Android. Join us to make a significant impact on our industry.",
            "Toronto, Canada",
            FULL_TIME,
            ONSITE,
            List.of("Ensure the best possible performance, quality, and responsiveness of the application.", "Work on bug fixing and improving application performance.", "Continuously discover, evaluate, and implement new technologies to maximize development efficiency.", "Collaborate with cross-functional teams to define, design, and ship new features.", "Integrate with RESTful APIs and third-party libraries.", "Maintain code quality, organization, and automatization."),
            List.of("Mobile UI", "Git", "Java", "iOS SDK", "Dart", "Objective-C"),
            List.of("How many years of experience do you have with Mobile UI?", "What are your salary expectations?"),
            87
        ),
        new SeedJobDefinition(
            "Mobile Architect",
            "Join our cross-platform team to deliver seamless experiences on iOS and Android. Collaborate with designers to implement pixel-perfect user interfaces. Build intuitive and performant mobile applications for millions of users. The ideal candidate thrives in a fast-paced setting.",
            "Paris, France",
            INTERNSHIP,
            HYBRID,
            List.of("Work on bug fixing and improving application performance.", "Unit-test code for robustness, including edge cases, usability, and general reliability.", "Maintain code quality, organization, and automatization.", "Integrate with RESTful APIs and third-party libraries."),
            List.of("REST APIs", "Objective-C", "Android SDK", "Swift", "Kotlin", "TypeScript", "JavaScript", "React Native", "Dart", "Git"),
            List.of("How many years of experience do you have with REST APIs?", "What are your salary expectations?", "Describe a complex Mobile Architect project you worked on."),
            11
        )
        );
    }

    /** Returns jobs for productBusinessJobs */
    public static List<SeedJobDefinition> productBusinessJobs() {
        return List.of(
        new SeedJobDefinition(
            "Product Manager",
            "We are looking for an experienced agile leader to drive team efficiency. Manage complex cross-functional projects and ensure timely delivery. Translate business requirements into detailed technical specifications. Join us to make a significant impact on our industry.",
            "Pune, India",
            FULL_TIME,
            ONSITE,
            List.of("Gather and analyze feedback from customers, stakeholders, and other teams.", "Write detailed product requirements, user stories, and acceptance criteria.", "Collaborate closely with engineering, design, and marketing teams.", "Facilitate agile ceremonies such as sprint planning, daily stand-ups, and retrospectives.", "Define product vision, strategy, and roadmap in alignment with business goals."),
            List.of("Stakeholder Management", "Scrum", "SQL", "Data Analysis", "Project Management", "Jira", "A/B Testing", "Kanban", "User Research"),
            List.of("How many years of experience do you have with User Research?", "What are your salary expectations?", "Describe a complex Product Manager project you worked on."),
            75
        ),
        new SeedJobDefinition(
            "Product Manager",
            "Lead the vision, strategy, and execution of our core product offerings. Join our team to analyze market trends and identify new product opportunities. The ideal candidate thrives in a fast-paced setting.",
            "Kolkata, India",
            FULL_TIME,
            HYBRID,
            List.of("Define product vision, strategy, and roadmap in alignment with business goals.", "Champion agile methodologies and drive continuous improvement.", "Facilitate agile ceremonies such as sprint planning, daily stand-ups, and retrospectives.", "Gather and analyze feedback from customers, stakeholders, and other teams."),
            List.of("A/B Testing", "Roadmapping", "Agile", "User Research", "Product Management", "Jira"),
            List.of("How many years of experience do you have with Agile?", "Describe a complex Product Manager project you worked on."),
            62
        ),
        new SeedJobDefinition(
            "Technical Product Manager",
            "Lead the vision, strategy, and execution of our core product offerings. You will bridge the gap between technical teams and business stakeholders. The ideal candidate thrives in a fast-paced setting.",
            "Denver, CO",
            FULL_TIME,
            ONSITE,
            List.of("Define product vision, strategy, and roadmap in alignment with business goals.", "Manage project scope, schedule, and risks to ensure successful delivery.", "Collaborate closely with engineering, design, and marketing teams.", "Facilitate agile ceremonies such as sprint planning, daily stand-ups, and retrospectives.", "Analyze business data and metrics to drive informed decision-making.", "Gather and analyze feedback from customers, stakeholders, and other teams.", "Write detailed product requirements, user stories, and acceptance criteria."),
            List.of("Kanban", "Confluence", "Roadmapping", "User Research", "Scrum"),
            List.of("How many years of experience do you have with User Research?", "What are your salary expectations?"),
            3
        ),
        new SeedJobDefinition(
            "Technical Product Manager",
            "Manage complex cross-functional projects and ensure timely delivery. Lead the vision, strategy, and execution of our core product offerings. You will be a key player in our growing, dynamic team.",
            "Mumbai, India",
            INTERNSHIP,
            HYBRID,
            List.of("Champion agile methodologies and drive continuous improvement.", "Write detailed product requirements, user stories, and acceptance criteria.", "Define product vision, strategy, and roadmap in alignment with business goals.", "Facilitate agile ceremonies such as sprint planning, daily stand-ups, and retrospectives.", "Manage project scope, schedule, and risks to ensure successful delivery.", "Collaborate closely with engineering, design, and marketing teams."),
            List.of("Confluence", "Scrum", "Product Management", "Agile", "Jira", "User Research", "A/B Testing", "Kanban", "Data Analysis", "SQL"),
            List.of("How many years of experience do you have with A/B Testing?"),
            19
        ),
        new SeedJobDefinition(
            "Business Analyst",
            "You will bridge the gap between technical teams and business stakeholders. We are looking for an experienced agile leader to drive team efficiency. Manage complex cross-functional projects and ensure timely delivery. You will be a key player in our growing, dynamic team.",
            "Chicago, IL",
            FULL_TIME,
            REMOTE,
            List.of("Manage project scope, schedule, and risks to ensure successful delivery.", "Analyze business data and metrics to drive informed decision-making.", "Define product vision, strategy, and roadmap in alignment with business goals.", "Collaborate closely with engineering, design, and marketing teams.", "Champion agile methodologies and drive continuous improvement.", "Gather and analyze feedback from customers, stakeholders, and other teams."),
            List.of("Product Management", "User Research", "Jira", "SQL", "A/B Testing"),
            List.of("How many years of experience do you have with Jira?"),
            68
        ),
        new SeedJobDefinition(
            "Business Analyst",
            "Translate business requirements into detailed technical specifications. Manage complex cross-functional projects and ensure timely delivery. We offer a collaborative and innovative work environment.",
            "Denver, CO",
            PART_TIME,
            ONSITE,
            List.of("Collaborate closely with engineering, design, and marketing teams.", "Write detailed product requirements, user stories, and acceptance criteria.", "Analyze business data and metrics to drive informed decision-making.", "Manage project scope, schedule, and risks to ensure successful delivery.", "Champion agile methodologies and drive continuous improvement.", "Define product vision, strategy, and roadmap in alignment with business goals.", "Gather and analyze feedback from customers, stakeholders, and other teams."),
            List.of("Data Analysis", "User Research", "Kanban", "Jira", "Product Management", "Confluence", "Roadmapping", "SQL", "Project Management"),
            List.of("How many years of experience do you have with User Research?"),
            88
        ),
        new SeedJobDefinition(
            "Product Analyst",
            "Lead the vision, strategy, and execution of our core product offerings. You will bridge the gap between technical teams and business stakeholders. We are looking for an experienced agile leader to drive team efficiency. Join us to make a significant impact on our industry.",
            "Noida, India",
            FULL_TIME,
            REMOTE,
            List.of("Collaborate closely with engineering, design, and marketing teams.", "Champion agile methodologies and drive continuous improvement.", "Facilitate agile ceremonies such as sprint planning, daily stand-ups, and retrospectives.", "Analyze business data and metrics to drive informed decision-making.", "Gather and analyze feedback from customers, stakeholders, and other teams.", "Write detailed product requirements, user stories, and acceptance criteria."),
            List.of("Agile", "Project Management", "SQL", "Data Analysis", "Jira", "Roadmapping", "Confluence", "Stakeholder Management"),
            List.of("How many years of experience do you have with Stakeholder Management?", "Describe a complex Product Analyst project you worked on."),
            42
        ),
        new SeedJobDefinition(
            "Project Manager",
            "Lead the vision, strategy, and execution of our core product offerings. We are looking for an experienced agile leader to drive team efficiency. Manage complex cross-functional projects and ensure timely delivery. We offer a collaborative and innovative work environment.",
            "Toronto, Canada",
            FULL_TIME,
            HYBRID,
            List.of("Analyze business data and metrics to drive informed decision-making.", "Champion agile methodologies and drive continuous improvement.", "Facilitate agile ceremonies such as sprint planning, daily stand-ups, and retrospectives.", "Define product vision, strategy, and roadmap in alignment with business goals.", "Manage project scope, schedule, and risks to ensure successful delivery.", "Collaborate closely with engineering, design, and marketing teams."),
            List.of("Jira", "Roadmapping", "Confluence", "Project Management", "User Research", "Data Analysis"),
            List.of("How many years of experience do you have with User Research?", "What are your salary expectations?", "Describe a complex Project Manager project you worked on."),
            71
        ),
        new SeedJobDefinition(
            "Project Manager",
            "We are looking for an experienced agile leader to drive team efficiency. Lead the vision, strategy, and execution of our core product offerings. Translate business requirements into detailed technical specifications. Join us to make a significant impact on our industry.",
            "Sydney, Australia",
            FULL_TIME,
            HYBRID,
            List.of("Collaborate closely with engineering, design, and marketing teams.", "Write detailed product requirements, user stories, and acceptance criteria.", "Manage project scope, schedule, and risks to ensure successful delivery.", "Define product vision, strategy, and roadmap in alignment with business goals.", "Analyze business data and metrics to drive informed decision-making.", "Gather and analyze feedback from customers, stakeholders, and other teams.", "Champion agile methodologies and drive continuous improvement."),
            List.of("Jira", "Roadmapping", "SQL", "Data Analysis", "Agile", "Scrum", "A/B Testing", "Product Management", "User Research", "Confluence"),
            List.of("How many years of experience do you have with Confluence?", "What are your salary expectations?"),
            34
        ),
        new SeedJobDefinition(
            "Program Manager",
            "Manage complex cross-functional projects and ensure timely delivery. Translate business requirements into detailed technical specifications. You will be a key player in our growing, dynamic team.",
            "Singapore",
            INTERNSHIP,
            ONSITE,
            List.of("Define product vision, strategy, and roadmap in alignment with business goals.", "Write detailed product requirements, user stories, and acceptance criteria.", "Analyze business data and metrics to drive informed decision-making.", "Gather and analyze feedback from customers, stakeholders, and other teams.", "Champion agile methodologies and drive continuous improvement.", "Facilitate agile ceremonies such as sprint planning, daily stand-ups, and retrospectives."),
            List.of("Project Management", "Jira", "Agile", "Data Analysis", "A/B Testing"),
            List.of("How many years of experience do you have with Project Management?", "What are your salary expectations?"),
            49
        ),
        new SeedJobDefinition(
            "Program Manager",
            "Join our team to analyze market trends and identify new product opportunities. We are looking for an experienced agile leader to drive team efficiency. Lead the vision, strategy, and execution of our core product offerings. Join us to make a significant impact on our industry.",
            "Toronto, Canada",
            PART_TIME,
            HYBRID,
            List.of("Define product vision, strategy, and roadmap in alignment with business goals.", "Write detailed product requirements, user stories, and acceptance criteria.", "Champion agile methodologies and drive continuous improvement.", "Analyze business data and metrics to drive informed decision-making.", "Gather and analyze feedback from customers, stakeholders, and other teams."),
            List.of("Project Management", "Data Analysis", "Scrum", "Agile", "SQL", "Kanban", "Product Management"),
            List.of("How many years of experience do you have with Agile?", "Describe a complex Program Manager project you worked on."),
            28
        ),
        new SeedJobDefinition(
            "Scrum Master",
            "You will bridge the gap between technical teams and business stakeholders. Manage complex cross-functional projects and ensure timely delivery. Translate business requirements into detailed technical specifications. We offer a collaborative and innovative work environment.",
            "Chicago, IL",
            CONTRACT,
            REMOTE,
            List.of("Define product vision, strategy, and roadmap in alignment with business goals.", "Write detailed product requirements, user stories, and acceptance criteria.", "Collaborate closely with engineering, design, and marketing teams.", "Facilitate agile ceremonies such as sprint planning, daily stand-ups, and retrospectives.", "Champion agile methodologies and drive continuous improvement."),
            List.of("Scrum", "Project Management", "Confluence", "Data Analysis", "A/B Testing", "Jira", "Product Management", "Roadmapping", "Agile"),
            List.of("How many years of experience do you have with Confluence?", "What are your salary expectations?"),
            22
        ),
        new SeedJobDefinition(
            "Agile Coach",
            "We are looking for an experienced agile leader to drive team efficiency. Join our team to analyze market trends and identify new product opportunities. You will bridge the gap between technical teams and business stakeholders. You will be a key player in our growing, dynamic team.",
            "Austin, TX",
            PART_TIME,
            HYBRID,
            List.of("Collaborate closely with engineering, design, and marketing teams.", "Gather and analyze feedback from customers, stakeholders, and other teams.", "Facilitate agile ceremonies such as sprint planning, daily stand-ups, and retrospectives.", "Write detailed product requirements, user stories, and acceptance criteria.", "Define product vision, strategy, and roadmap in alignment with business goals.", "Manage project scope, schedule, and risks to ensure successful delivery.", "Analyze business data and metrics to drive informed decision-making."),
            List.of("Product Management", "Kanban", "User Research", "Stakeholder Management", "Project Management", "Data Analysis"),
            List.of("How many years of experience do you have with Product Management?", "What are your salary expectations?"),
            82
        )
        );
    }

    /** Returns jobs for designJobs */
    public static List<SeedJobDefinition> designJobs() {
        return List.of(
        new SeedJobDefinition(
            "UI/UX Designer",
            "Join our design team to build and maintain a scalable design system. You will conduct user research to understand customer needs and behaviors. Bring our products to life with engaging motion graphics and micro-interactions. The ideal candidate thrives in a fast-paced setting.",
            "Austin, TX",
            CONTRACT,
            REMOTE,
            List.of("Establish and promote design guidelines, best practices, and standards.", "Present and defend design decisions to peers and executive level stakeholders.", "Develop and maintain comprehensive design systems and component libraries.", "Design polished user interfaces and prototypes for web and mobile applications.", "Collaborate closely with product managers and engineers to implement designs.", "Create compelling visual assets and motion graphics for marketing and product.", "Create wireframes, storyboards, user flows, and site maps to communicate interaction and design ideas."),
            List.of("Design Systems", "InVision", "Figma", "HTML/CSS", "UX Design", "Motion Design", "Wireframing", "UI Design", "Adobe Creative Suite", "Usability Testing"),
            List.of("How many years of experience do you have with InVision?"),
            26
        ),
        new SeedJobDefinition(
            "UI/UX Designer",
            "You will conduct user research to understand customer needs and behaviors. Create intuitive, engaging, and beautiful user experiences for our products. We need a creative visionary to lead our design strategy and brand identity. You will be a key player in our growing, dynamic team.",
            "Kolkata, India",
            INTERNSHIP,
            REMOTE,
            List.of("Create compelling visual assets and motion graphics for marketing and product.", "Conduct user research, interviews, and usability testing to gather insights.", "Collaborate closely with product managers and engineers to implement designs.", "Present and defend design decisions to peers and executive level stakeholders.", "Establish and promote design guidelines, best practices, and standards."),
            List.of("InVision", "UI Design", "HTML/CSS", "Wireframing", "UX Design", "Design Systems", "Sketch", "Usability Testing", "User Research", "Prototyping"),
            List.of("How many years of experience do you have with InVision?", "What are your salary expectations?", "Describe a complex UI/UX Designer project you worked on."),
            10
        ),
        new SeedJobDefinition(
            "Product Designer",
            "Join our design team to build and maintain a scalable design system. Translate complex product requirements into simple, elegant user interfaces. Bring our products to life with engaging motion graphics and micro-interactions. You will be a key player in our growing, dynamic team.",
            "Abu Dhabi, UAE",
            INTERNSHIP,
            ONSITE,
            List.of("Establish and promote design guidelines, best practices, and standards.", "Develop and maintain comprehensive design systems and component libraries.", "Create wireframes, storyboards, user flows, and site maps to communicate interaction and design ideas.", "Conduct user research, interviews, and usability testing to gather insights.", "Design polished user interfaces and prototypes for web and mobile applications.", "Present and defend design decisions to peers and executive level stakeholders.", "Collaborate closely with product managers and engineers to implement designs."),
            List.of("HTML/CSS", "Design Systems", "UX Design", "Usability Testing", "Sketch", "User Research"),
            List.of("How many years of experience do you have with Sketch?", "Describe a complex Product Designer project you worked on."),
            85
        ),
        new SeedJobDefinition(
            "Product Designer",
            "Create intuitive, engaging, and beautiful user experiences for our products. Join our design team to build and maintain a scalable design system. Translate complex product requirements into simple, elegant user interfaces. The ideal candidate thrives in a fast-paced setting.",
            "London, UK",
            FULL_TIME,
            HYBRID,
            List.of("Present and defend design decisions to peers and executive level stakeholders.", "Conduct user research, interviews, and usability testing to gather insights.", "Develop and maintain comprehensive design systems and component libraries.", "Create wireframes, storyboards, user flows, and site maps to communicate interaction and design ideas."),
            List.of("HTML/CSS", "Wireframing", "UI Design", "Adobe Creative Suite", "Sketch", "Motion Design", "User Research", "InVision", "UX Design"),
            List.of("How many years of experience do you have with Sketch?", "What are your salary expectations?"),
            32
        ),
        new SeedJobDefinition(
            "UX Researcher",
            "Create intuitive, engaging, and beautiful user experiences for our products. You will conduct user research to understand customer needs and behaviors. Join our design team to build and maintain a scalable design system. You will be a key player in our growing, dynamic team.",
            "Munich, Germany",
            FULL_TIME,
            ONSITE,
            List.of("Conduct user research, interviews, and usability testing to gather insights.", "Design polished user interfaces and prototypes for web and mobile applications.", "Establish and promote design guidelines, best practices, and standards.", "Create wireframes, storyboards, user flows, and site maps to communicate interaction and design ideas.", "Collaborate closely with product managers and engineers to implement designs."),
            List.of("Figma", "Design Systems", "Wireframing", "HTML/CSS", "UI Design"),
            List.of("How many years of experience do you have with Figma?", "Describe a complex UX Researcher project you worked on."),
            17
        ),
        new SeedJobDefinition(
            "UX Researcher",
            "Join our design team to build and maintain a scalable design system. Create intuitive, engaging, and beautiful user experiences for our products. Bring our products to life with engaging motion graphics and micro-interactions. The ideal candidate thrives in a fast-paced setting.",
            "Bengaluru, India",
            FULL_TIME,
            HYBRID,
            List.of("Conduct user research, interviews, and usability testing to gather insights.", "Develop and maintain comprehensive design systems and component libraries.", "Create compelling visual assets and motion graphics for marketing and product.", "Create wireframes, storyboards, user flows, and site maps to communicate interaction and design ideas.", "Establish and promote design guidelines, best practices, and standards.", "Collaborate closely with product managers and engineers to implement designs."),
            List.of("Design Systems", "Usability Testing", "Adobe Creative Suite", "Figma", "Sketch", "Prototyping"),
            List.of("How many years of experience do you have with Usability Testing?"),
            78
        ),
        new SeedJobDefinition(
            "Visual Designer",
            "You will conduct user research to understand customer needs and behaviors. Create intuitive, engaging, and beautiful user experiences for our products. The ideal candidate thrives in a fast-paced setting.",
            "Stockholm, Sweden",
            FULL_TIME,
            REMOTE,
            List.of("Design polished user interfaces and prototypes for web and mobile applications.", "Establish and promote design guidelines, best practices, and standards.", "Present and defend design decisions to peers and executive level stakeholders.", "Develop and maintain comprehensive design systems and component libraries.", "Create compelling visual assets and motion graphics for marketing and product."),
            List.of("Wireframing", "Sketch", "Motion Design", "HTML/CSS", "User Research", "Prototyping", "Figma", "InVision", "Adobe Creative Suite", "UX Design"),
            List.of("How many years of experience do you have with Sketch?"),
            37
        ),
        new SeedJobDefinition(
            "Interaction Designer",
            "We need a creative visionary to lead our design strategy and brand identity. Create intuitive, engaging, and beautiful user experiences for our products. Join our design team to build and maintain a scalable design system. You will be a key player in our growing, dynamic team.",
            "Mumbai, India",
            INTERNSHIP,
            REMOTE,
            List.of("Create compelling visual assets and motion graphics for marketing and product.", "Establish and promote design guidelines, best practices, and standards.", "Create wireframes, storyboards, user flows, and site maps to communicate interaction and design ideas.", "Design polished user interfaces and prototypes for web and mobile applications.", "Conduct user research, interviews, and usability testing to gather insights.", "Develop and maintain comprehensive design systems and component libraries.", "Present and defend design decisions to peers and executive level stakeholders."),
            List.of("Design Systems", "Usability Testing", "Adobe Creative Suite", "InVision", "Figma", "Motion Design", "User Research"),
            List.of("How many years of experience do you have with InVision?"),
            3
        ),
        new SeedJobDefinition(
            "Design Systems Engineer",
            "Join our design team to build and maintain a scalable design system. Translate complex product requirements into simple, elegant user interfaces. The ideal candidate thrives in a fast-paced setting.",
            "Berlin, Germany",
            FULL_TIME,
            ONSITE,
            List.of("Design polished user interfaces and prototypes for web and mobile applications.", "Create wireframes, storyboards, user flows, and site maps to communicate interaction and design ideas.", "Conduct user research, interviews, and usability testing to gather insights.", "Establish and promote design guidelines, best practices, and standards.", "Develop and maintain comprehensive design systems and component libraries.", "Present and defend design decisions to peers and executive level stakeholders."),
            List.of("Sketch", "UI Design", "InVision", "Figma", "Adobe Creative Suite"),
            List.of("How many years of experience do you have with Adobe Creative Suite?"),
            66
        ),
        new SeedJobDefinition(
            "Creative Director",
            "Bring our products to life with engaging motion graphics and micro-interactions. Translate complex product requirements into simple, elegant user interfaces. You will be a key player in our growing, dynamic team.",
            "Abu Dhabi, UAE",
            FULL_TIME,
            HYBRID,
            List.of("Develop and maintain comprehensive design systems and component libraries.", "Present and defend design decisions to peers and executive level stakeholders.", "Create wireframes, storyboards, user flows, and site maps to communicate interaction and design ideas.", "Collaborate closely with product managers and engineers to implement designs.", "Create compelling visual assets and motion graphics for marketing and product."),
            List.of("Usability Testing", "Motion Design", "Sketch", "User Research", "Figma", "UX Design", "HTML/CSS", "InVision", "Prototyping", "Adobe Creative Suite"),
            List.of("How many years of experience do you have with Figma?"),
            41
        ),
        new SeedJobDefinition(
            "Motion Designer",
            "Create intuitive, engaging, and beautiful user experiences for our products. You will conduct user research to understand customer needs and behaviors. You will be a key player in our growing, dynamic team.",
            "Dublin, Ireland",
            FULL_TIME,
            ONSITE,
            List.of("Present and defend design decisions to peers and executive level stakeholders.", "Collaborate closely with product managers and engineers to implement designs.", "Conduct user research, interviews, and usability testing to gather insights.", "Establish and promote design guidelines, best practices, and standards.", "Create wireframes, storyboards, user flows, and site maps to communicate interaction and design ideas."),
            List.of("User Research", "Wireframing", "Prototyping", "Motion Design", "Adobe Creative Suite", "Figma"),
            List.of("How many years of experience do you have with Adobe Creative Suite?"),
            84
        )
        );
    }

    /** Returns jobs for qaAndOtherJobs */
    public static List<SeedJobDefinition> qaAndOtherJobs() {
        return List.of(
        new SeedJobDefinition(
            "Senior Quality Assurance Engineer",
            "Lead our QA efforts and establish robust testing protocols. You will be responsible for end-to-end testing of complex software systems. We value attention to detail and a passion for quality.",
            "London, UK",
            FULL_TIME,
            HYBRID,
            List.of("Develop and execute comprehensive test plans.", "Mentor junior QA engineers and share best practices.", "Automate regression test suites using Selenium and Appium.", "Collaborate with developers to resolve defects.", "Perform security and performance testing on web applications."),
            List.of("Selenium", "Appium", "JMeter", "Java", "Test Automation", "CI/CD"),
            List.of("How many years of test automation experience do you have?"),
            10
        ),
        new SeedJobDefinition(
            "Game Physics Programmer",
            "Join our award-winning game studio to develop cutting-edge physics engines. You will optimize collision detection and rigid body dynamics for next-gen consoles. We offer a creative and challenging environment.",
            "Tokyo, Japan",
            FULL_TIME,
            ONSITE,
            List.of("Implement and optimize game physics systems.", "Profile and improve performance of collision detection algorithms.", "Work closely with designers to achieve the desired gameplay feel.", "Debug complex math and physics issues in a large C++ codebase."),
            List.of("C++", "Mathematics", "Physics", "Unreal Engine", "Multithreading"),
            List.of("Describe your experience with rigid body dynamics in game engines."),
            21
        ),
        new SeedJobDefinition(
            "Blockchain Solutions Architect",
            "Design scalable and secure decentralized applications for enterprise clients. You will evaluate different blockchain protocols and recommend optimal technical solutions. Join a team at the forefront of Web3 innovation.",
            "Singapore",
            CONTRACT,
            REMOTE,
            List.of("Architect enterprise-grade blockchain solutions.", "Write and audit smart contracts in Solidity and Rust.", "Integrate blockchain networks with legacy backend systems.", "Lead technical workshops with enterprise clients."),
            List.of("Solidity", "Rust", "Ethereum", "Cryptography", "System Architecture", "Node.js"),
            List.of("Provide an example of a smart contract you audited for security vulnerabilities."),
            45
        ),
        new SeedJobDefinition(
            "Lead Network Architect",
            "Oversee the design and implementation of our global network infrastructure. You will ensure high availability and security across all data centers. We need a visionary leader for our growing infrastructure team.",
            "Denver, CO",
            FULL_TIME,
            HYBRID,
            List.of("Design scalable global network topologies.", "Implement Zero Trust security models across the corporate network.", "Manage relationships with ISPs and hardware vendors.", "Conduct capacity planning and performance tuning.", "Lead troubleshooting for critical network outages."),
            List.of("BGP", "OSPF", "Cisco NX-OS", "Network Security", "Python", "Automation"),
            List.of("How many years of experience do you have managing global enterprise networks?"),
            8
        ),
        new SeedJobDefinition(
            "Robotics Software Engineer",
            "Develop control software for autonomous mobile robots used in logistics. You will work on path planning, obstacle avoidance, and sensor fusion. Help us revolutionize warehouse automation.",
            "Boston, MA",
            FULL_TIME,
            ONSITE,
            List.of("Develop path planning algorithms for mobile robots.", "Integrate LiDAR and camera data for obstacle avoidance.", "Write efficient C++ code for embedded control systems.", "Simulate robot behavior in Gazebo or similar environments.", "Conduct field testing and debugging of physical robots."),
            List.of("C++", "ROS", "Python", "Computer Vision", "Control Systems", "Linux"),
            List.of("Describe a path planning algorithm you have implemented."),
            33
        ),
        new SeedJobDefinition(
            "AR/VR Developer",
            "Create immersive augmented and virtual reality experiences for enterprise training. You will leverage Unity and Unreal Engine to build realistic simulations. Join a forward-thinking team pushing the boundaries of spatial computing.",
            "Los Angeles, CA",
            CONTRACT,
            REMOTE,
            List.of("Develop AR/VR applications using Unity and C#.", "Optimize 3D assets and shaders for mobile VR headsets.", "Implement intuitive spatial UI/UX designs.", "Integrate multiplayer networking for collaborative simulations.", "Stay updated on the latest AR/VR hardware capabilities."),
            List.of("Unity", "C#", "Unreal Engine", "3D Math", "Shader Graph", "Photon Networking"),
            List.of("Which AR/VR headsets have you developed for?"),
            62
        ),
        new SeedJobDefinition(
            "Technical Evangelist",
            "Champion our developer tools and APIs to the global software engineering community. You will write technical blog posts, speak at conferences, and build sample applications. We are looking for a passionate developer who loves teaching others.",
            "San Francisco, CA",
            FULL_TIME,
            REMOTE,
            List.of("Create engaging technical content including tutorials and blog posts.", "Speak at developer conferences and meetups worldwide.", "Build open-source sample applications showcasing our APIs.", "Gather feedback from the community to improve developer experience.", "Collaborate with product and marketing teams on launch strategies."),
            List.of("Public Speaking", "Technical Writing", "JavaScript", "Python", "API Design", "Community Building"),
            List.of("Please provide links to your technical writing or speaking engagements."),
            12
        ),
        new SeedJobDefinition(
            "Embedded Linux Engineer",
            "Develop custom Linux distributions and drivers for smart home devices. You will optimize system performance and ensure secure over-the-air updates. Join a fast-paced hardware startup.",
            "Taipei, Taiwan",
            FULL_TIME,
            ONSITE,
            List.of("Customize Yocto or Buildroot for new hardware platforms.", "Write and debug Linux device drivers (I2C, SPI, UART).", "Optimize system boot time and power consumption.", "Implement secure OTA update mechanisms.", "Collaborate with hardware engineers during board bring-up."),
            List.of("Linux Kernel", "C", "Yocto", "Device Drivers", "U-Boot", "Hardware Debugging"),
            List.of("Describe your experience with Yocto Project."),
            77
        ),
        new SeedJobDefinition(
            "Quantum Computing Researcher",
            "Conduct cutting-edge research in quantum algorithms and error correction. You will collaborate with leading academics to publish papers and develop proprietary quantum software. Be at the forefront of the next computing revolution.",
            "Zurich, Switzerland",
            FULL_TIME,
            HYBRID,
            List.of("Develop novel quantum algorithms for optimization and simulation.", "Research and implement quantum error correction codes.", "Publish findings in peer-reviewed scientific journals.", "Collaborate with hardware teams to understand qubit constraints.", "Prototype quantum software using Qiskit or Cirq."),
            List.of("Quantum Mechanics", "Linear Algebra", "Python", "Qiskit", "Research", "Algorithms"),
            List.of("Please provide a list of your relevant academic publications."),
            4
        ),
        new SeedJobDefinition(
            "Bioinformatics Data Scientist",
            "Analyze large-scale genomic datasets to identify disease biomarkers. You will develop machine learning models to predict patient responses to therapies. Join a biotechnology company dedicated to personalized medicine.",
            "Cambridge, UK",
            FULL_TIME,
            HYBRID,
            List.of("Process and analyze next-generation sequencing (NGS) data.", "Develop predictive machine learning models for clinical outcomes.", "Create interactive data visualizations for researchers.", "Maintain scalable bioinformatics pipelines on AWS.", "Collaborate with biologists to interpret complex datasets."),
            List.of("Python", "R", "Genomics", "Machine Learning", "AWS", "Data Visualization", "Biostatistics"),
            List.of("Describe a complex genomic dataset you have analyzed."),
            29
        ),

        new SeedJobDefinition(
            "QA Engineer",
            "Manage and optimize our enterprise IT infrastructure and corporate networks. We need a technical expert to design custom solutions for our enterprise clients. Ensure the quality and reliability of our software products through rigorous testing. The ideal candidate thrives in a fast-paced setting.",
            "Seattle, WA",
            INTERNSHIP,
            HYBRID,
            List.of("Design and implement secure smart contracts on blockchain platforms.", "Collaborate with sales and engineering to design technical solutions for clients.", "Configure, monitor, and maintain corporate networks and IT systems.", "Write high-quality technical documentation, API guides, and user manuals.", "Provide tier 3 technical support and resolve escalated customer issues."),
            List.of("Unity", "Customer Support", "RTOS", "C++", "TestNG"),
            List.of("How many years of experience do you have with RTOS?", "What are your salary expectations?", "Describe a complex QA Engineer project you worked on."),
            53
        ),
        new SeedJobDefinition(
            "QA Engineer",
            "Provide exceptional technical support and troubleshoot complex customer issues. You will develop automated test frameworks to accelerate our release cycles. We offer a collaborative and innovative work environment.",
            "Dublin, Ireland",
            FULL_TIME,
            ONSITE,
            List.of("Develop engaging gameplay mechanics and optimize game engine performance.", "Provide tier 3 technical support and resolve escalated customer issues.", "Configure, monitor, and maintain corporate networks and IT systems.", "Collaborate with sales and engineering to design technical solutions for clients.", "Design and implement secure smart contracts on blockchain platforms.", "Perform manual and exploratory testing to identify software defects."),
            List.of("Unity", "Solidity", "Cisco", "Technical Writing", "C", "Markdown", "Cypress", "TestNG", "Postman", "Unreal Engine"),
            List.of("How many years of experience do you have with Unreal Engine?"),
            19
        ),
        new SeedJobDefinition(
            "QA Engineer",
            "We need a technical expert to design custom solutions for our enterprise clients. Join our team to create clear, comprehensive technical documentation for developers. The ideal candidate thrives in a fast-paced setting.",
            "Gurugram, India",
            FULL_TIME,
            HYBRID,
            List.of("Develop engaging gameplay mechanics and optimize game engine performance.", "Perform manual and exploratory testing to identify software defects.", "Design and implement comprehensive test plans and automated test scripts.", "Collaborate with sales and engineering to design technical solutions for clients.", "Design and implement secure smart contracts on blockchain platforms."),
            List.of("Technical Writing", "JUnit", "C++", "Postman", "Solidity", "Windows Server"),
            List.of("How many years of experience do you have with Postman?", "What are your salary expectations?", "Describe a complex QA Engineer project you worked on."),
            37
        ),
        new SeedJobDefinition(
            "Automation Engineer",
            "We need a technical expert to design custom solutions for our enterprise clients. You will develop automated test frameworks to accelerate our release cycles. Ensure the quality and reliability of our software products through rigorous testing. The ideal candidate thrives in a fast-paced setting.",
            "Bengaluru, India",
            FULL_TIME,
            ONSITE,
            List.of("Write high-quality technical documentation, API guides, and user manuals.", "Perform manual and exploratory testing to identify software defects.", "Design and implement comprehensive test plans and automated test scripts.", "Configure, monitor, and maintain corporate networks and IT systems."),
            List.of("Customer Support", "Unreal Engine", "Markdown", "Selenium", "C", "Cypress", "TestNG", "Linux Administration", "JUnit", "Networking"),
            List.of("How many years of experience do you have with JUnit?", "What are your salary expectations?"),
            81
        ),
        new SeedJobDefinition(
            "Automation Engineer",
            "Provide exceptional technical support and troubleshoot complex customer issues. Manage and optimize our enterprise IT infrastructure and corporate networks. Join our team to create clear, comprehensive technical documentation for developers. The ideal candidate thrives in a fast-paced setting.",
            "Amsterdam, Netherlands",
            CONTRACT,
            ONSITE,
            List.of("Write high-quality technical documentation, API guides, and user manuals.", "Configure, monitor, and maintain corporate networks and IT systems.", "Collaborate with sales and engineering to design technical solutions for clients.", "Perform manual and exploratory testing to identify software defects.", "Provide tier 3 technical support and resolve escalated customer issues."),
            List.of("Cypress", "RTOS", "Selenium", "Unreal Engine", "Unity", "Technical Writing"),
            List.of("How many years of experience do you have with Cypress?", "What are your salary expectations?"),
            85
        ),
        new SeedJobDefinition(
            "Technical Writer",
            "You will develop automated test frameworks to accelerate our release cycles. Join our team to create clear, comprehensive technical documentation for developers. The ideal candidate thrives in a fast-paced setting.",
            "Los Angeles, CA",
            FULL_TIME,
            REMOTE,
            List.of("Write high-quality technical documentation, API guides, and user manuals.", "Perform manual and exploratory testing to identify software defects.", "Collaborate with sales and engineering to design technical solutions for clients.", "Design and implement comprehensive test plans and automated test scripts.", "Develop engaging gameplay mechanics and optimize game engine performance.", "Design and implement secure smart contracts on blockchain platforms."),
            List.of("Unity", "Linux Administration", "Technical Writing", "Customer Support", "TestNG", "Markdown", "Cypress"),
            List.of("How many years of experience do you have with Customer Support?", "What are your salary expectations?"),
            75
        ),
        new SeedJobDefinition(
            "Technical Writer",
            "Ensure the quality and reliability of our software products through rigorous testing. Manage and optimize our enterprise IT infrastructure and corporate networks. Join us to make a significant impact on our industry.",
            "Los Angeles, CA",
            CONTRACT,
            HYBRID,
            List.of("Write high-quality technical documentation, API guides, and user manuals.", "Develop engaging gameplay mechanics and optimize game engine performance.", "Collaborate with sales and engineering to design technical solutions for clients.", "Design and implement comprehensive test plans and automated test scripts.", "Provide tier 3 technical support and resolve escalated customer issues.", "Perform manual and exploratory testing to identify software defects."),
            List.of("TestNG", "C++", "Cisco", "Cypress", "Linux Administration", "Postman", "Networking", "JUnit"),
            List.of("How many years of experience do you have with Postman?", "What are your salary expectations?"),
            3
        ),
        new SeedJobDefinition(
            "Solutions Engineer",
            "Manage and optimize our enterprise IT infrastructure and corporate networks. Ensure the quality and reliability of our software products through rigorous testing. Provide exceptional technical support and troubleshoot complex customer issues. The ideal candidate thrives in a fast-paced setting.",
            "Denver, CO",
            PART_TIME,
            HYBRID,
            List.of("Provide tier 3 technical support and resolve escalated customer issues.", "Perform manual and exploratory testing to identify software defects.", "Develop engaging gameplay mechanics and optimize game engine performance.", "Design and implement secure smart contracts on blockchain platforms.", "Write high-quality technical documentation, API guides, and user manuals.", "Design and implement comprehensive test plans and automated test scripts.", "Collaborate with sales and engineering to design technical solutions for clients."),
            List.of("Postman", "Linux Administration", "JUnit", "Cypress", "Cisco", "Customer Support", "Markdown", "TestNG", "RTOS", "Selenium"),
            List.of("How many years of experience do you have with Linux Administration?", "What are your salary expectations?"),
            38
        ),
        new SeedJobDefinition(
            "Solutions Architect",
            "Ensure the quality and reliability of our software products through rigorous testing. Manage and optimize our enterprise IT infrastructure and corporate networks. Join our team to create clear, comprehensive technical documentation for developers. Join us to make a significant impact on our industry.",
            "Kochi, India",
            PART_TIME,
            REMOTE,
            List.of("Provide tier 3 technical support and resolve escalated customer issues.", "Collaborate with sales and engineering to design technical solutions for clients.", "Design and implement secure smart contracts on blockchain platforms.", "Develop engaging gameplay mechanics and optimize game engine performance."),
            List.of("C++", "Technical Writing", "C", "Solidity", "Linux Administration", "JUnit", "Cypress", "Selenium", "Unity"),
            List.of("How many years of experience do you have with C?", "What are your salary expectations?"),
            2
        ),
        new SeedJobDefinition(
            "Solutions Architect",
            "You will develop automated test frameworks to accelerate our release cycles. Provide exceptional technical support and troubleshoot complex customer issues. You will be a key player in our growing, dynamic team.",
            "New York, NY",
            FULL_TIME,
            HYBRID,
            List.of("Configure, monitor, and maintain corporate networks and IT systems.", "Provide tier 3 technical support and resolve escalated customer issues.", "Develop engaging gameplay mechanics and optimize game engine performance.", "Write high-quality technical documentation, API guides, and user manuals.", "Design and implement secure smart contracts on blockchain platforms.", "Collaborate with sales and engineering to design technical solutions for clients.", "Design and implement comprehensive test plans and automated test scripts."),
            List.of("RTOS", "Solidity", "Postman", "Selenium", "Networking", "C", "Cisco", "TestNG", "Customer Support", "Unity"),
            List.of("How many years of experience do you have with Unity?"),
            26
        ),
        new SeedJobDefinition(
            "Technical Support Engineer",
            "Manage and optimize our enterprise IT infrastructure and corporate networks. Ensure the quality and reliability of our software products through rigorous testing. Join us to make a significant impact on our industry.",
            "San Diego, CA",
            FULL_TIME,
            REMOTE,
            List.of("Design and implement secure smart contracts on blockchain platforms.", "Perform manual and exploratory testing to identify software defects.", "Develop engaging gameplay mechanics and optimize game engine performance.", "Collaborate with sales and engineering to design technical solutions for clients.", "Design and implement comprehensive test plans and automated test scripts.", "Configure, monitor, and maintain corporate networks and IT systems."),
            List.of("Cypress", "Linux Administration", "Unity", "RTOS", "Windows Server", "TestNG", "Technical Writing"),
            List.of("How many years of experience do you have with Cypress?", "What are your salary expectations?", "Describe a complex Technical Support Engineer project you worked on."),
            7
        ),
        new SeedJobDefinition(
            "Network Engineer",
            "Manage and optimize our enterprise IT infrastructure and corporate networks. We need a technical expert to design custom solutions for our enterprise clients. You will develop automated test frameworks to accelerate our release cycles. Join us to make a significant impact on our industry.",
            "Kochi, India",
            PART_TIME,
            REMOTE,
            List.of("Design and implement secure smart contracts on blockchain platforms.", "Write high-quality technical documentation, API guides, and user manuals.", "Perform manual and exploratory testing to identify software defects.", "Configure, monitor, and maintain corporate networks and IT systems."),
            List.of("RTOS", "JUnit", "Selenium", "C++", "Networking", "Cypress", "Windows Server", "Linux Administration", "TestNG"),
            List.of("How many years of experience do you have with C++?", "What are your salary expectations?"),
            8
        ),
        new SeedJobDefinition(
            "Systems Administrator",
            "Manage and optimize our enterprise IT infrastructure and corporate networks. We need a technical expert to design custom solutions for our enterprise clients. Provide exceptional technical support and troubleshoot complex customer issues. The ideal candidate thrives in a fast-paced setting.",
            "Los Angeles, CA",
            INTERNSHIP,
            ONSITE,
            List.of("Design and implement comprehensive test plans and automated test scripts.", "Configure, monitor, and maintain corporate networks and IT systems.", "Write high-quality technical documentation, API guides, and user manuals.", "Perform manual and exploratory testing to identify software defects.", "Collaborate with sales and engineering to design technical solutions for clients."),
            List.of("JUnit", "Linux Administration", "RTOS", "Markdown", "TestNG", "Selenium", "Windows Server", "Customer Support", "Cypress", "Solidity"),
            List.of("How many years of experience do you have with TestNG?"),
            7
        ),
        new SeedJobDefinition(
            "Game Developer",
            "Ensure the quality and reliability of our software products through rigorous testing. You will develop automated test frameworks to accelerate our release cycles. Join our team to create clear, comprehensive technical documentation for developers. Join us to make a significant impact on our industry.",
            "Toronto, Canada",
            FULL_TIME,
            REMOTE,
            List.of("Write high-quality technical documentation, API guides, and user manuals.", "Perform manual and exploratory testing to identify software defects.", "Collaborate with sales and engineering to design technical solutions for clients.", "Design and implement secure smart contracts on blockchain platforms.", "Provide tier 3 technical support and resolve escalated customer issues.", "Design and implement comprehensive test plans and automated test scripts."),
            List.of("Selenium", "Linux Administration", "Technical Writing", "Postman", "Solidity", "C", "Unreal Engine", "Windows Server", "Cypress"),
            List.of("How many years of experience do you have with Solidity?", "What are your salary expectations?"),
            44
        ),
        new SeedJobDefinition(
            "Game Developer",
            "You will develop automated test frameworks to accelerate our release cycles. Manage and optimize our enterprise IT infrastructure and corporate networks. You will be a key player in our growing, dynamic team.",
            "Sydney, Australia",
            FULL_TIME,
            ONSITE,
            List.of("Configure, monitor, and maintain corporate networks and IT systems.", "Provide tier 3 technical support and resolve escalated customer issues.", "Develop engaging gameplay mechanics and optimize game engine performance.", "Collaborate with sales and engineering to design technical solutions for clients.", "Design and implement secure smart contracts on blockchain platforms.", "Perform manual and exploratory testing to identify software defects."),
            List.of("JUnit", "RTOS", "Unity", "Cisco", "Customer Support", "TestNG", "Cypress", "C++", "Technical Writing"),
            List.of("How many years of experience do you have with Technical Writing?"),
            32
        ),
        new SeedJobDefinition(
            "Embedded Systems Engineer",
            "Join our team to create clear, comprehensive technical documentation for developers. You will develop automated test frameworks to accelerate our release cycles. We need a technical expert to design custom solutions for our enterprise clients. You will be a key player in our growing, dynamic team.",
            "Mumbai, India",
            INTERNSHIP,
            HYBRID,
            List.of("Write high-quality technical documentation, API guides, and user manuals.", "Design and implement secure smart contracts on blockchain platforms.", "Perform manual and exploratory testing to identify software defects.", "Collaborate with sales and engineering to design technical solutions for clients.", "Configure, monitor, and maintain corporate networks and IT systems.", "Design and implement comprehensive test plans and automated test scripts."),
            List.of("Networking", "Windows Server", "C", "Markdown", "Linux Administration", "TestNG", "JUnit", "Customer Support"),
            List.of("How many years of experience do you have with Windows Server?", "What are your salary expectations?"),
            44
        ),
        new SeedJobDefinition(
            "Blockchain Developer",
            "Join our team to create clear, comprehensive technical documentation for developers. We need a technical expert to design custom solutions for our enterprise clients. Join us to make a significant impact on our industry.",
            "Pune, India",
            CONTRACT,
            REMOTE,
            List.of("Provide tier 3 technical support and resolve escalated customer issues.", "Configure, monitor, and maintain corporate networks and IT systems.", "Collaborate with sales and engineering to design technical solutions for clients.", "Write high-quality technical documentation, API guides, and user manuals.", "Perform manual and exploratory testing to identify software defects.", "Design and implement secure smart contracts on blockchain platforms.", "Develop engaging gameplay mechanics and optimize game engine performance."),
            List.of("Unity", "Customer Support", "Selenium", "Unreal Engine", "Postman", "Technical Writing", "Cypress", "Solidity", "Cisco"),
            List.of("How many years of experience do you have with Selenium?"),
            62
        ),
        new SeedJobDefinition(
            "IT Support Specialist",
            "You will develop automated test frameworks to accelerate our release cycles. Provide exceptional technical support and troubleshoot complex customer issues. Ensure the quality and reliability of our software products through rigorous testing. The ideal candidate thrives in a fast-paced setting.",
            "Atlanta, GA",
            CONTRACT,
            REMOTE,
            List.of("Design and implement comprehensive test plans and automated test scripts.", "Write high-quality technical documentation, API guides, and user manuals.", "Configure, monitor, and maintain corporate networks and IT systems.", "Perform manual and exploratory testing to identify software defects.", "Collaborate with sales and engineering to design technical solutions for clients.", "Design and implement secure smart contracts on blockchain platforms."),
            List.of("Solidity", "C", "Windows Server", "RTOS", "Postman", "Cisco", "Selenium"),
            List.of("How many years of experience do you have with Windows Server?"),
            35
        )
        );
    }
}