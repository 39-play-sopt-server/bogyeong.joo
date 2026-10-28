package org.example.post.controller.dto;

import org.example.post.domain.Post;

import java.util.List;

public record PostListResponse(List<Post> posts, String nextCursor, boolean hasNext) {
}
