# Examples of Spring batch
* Fault tolerance
```java
@Bean
    public Step faultTolerantStep(ItemReader<String> reader, ItemProcessor<String, String> processor, ItemWriter<String> writer) {
        return stepBuilderFactory.get("faultTolerantStep")
            .<String, String>chunk(10)
            .reader(reader)
            .processor(processor)
            .writer(writer)
            
            // Skip configuration
            .faultTolerant()
            .skip(Exception.class)  // Skip when any Exception is encountered
            .skipLimit(5)           // Allow up to 5 skipped items
            
            // Retry configuration
            .retry(Exception.class)  // Retry on Exception
            .retryLimit(3)           // Retry a maximum of 3 times
            
            .build();
    }

    @Bean
    public RetryTemplate retryTemplate() {
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(3);  // Maximum 3 retry attempts

        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(retryPolicy);

        return retryTemplate;
    }
```