package com.justdo.plug.post.elastic;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface PostElasticsearchRepository extends ElasticsearchRepository<PostDocument, String> {

}
