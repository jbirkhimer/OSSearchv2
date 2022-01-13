<template>
  <div class="container">
    <header class="jumbotron">
      <h3>{{ content }}</h3>
    </header>
  </div>
  <a role="button"
     class="bi-info-circle-fill" data-bs-toggle="collapse" data-bs-target="#collapseIncludeFieldsInfo4"
     aria-expanded="false" aria-controls="collapseIncludeFieldsInfo4"></a>
  <div class="collapse" id="collapseIncludeFieldsInfo4">
    <div class="card card-body">
      <div class="form-text">
        <p class="lh-sm">
          By default search results will be returned with <b>Title</b>, <b>Snippet</b>, and <b>URL</b>.
          Site owners can optionally select <b>Anchor</b>, <b>Meta Description</b> and <b>Meta Keywords</b> to
          be
          returned with the search results.</p>
        <p class="lh-sm"><b>Meta Description</b> and <b>Meta Keywords</b> are metatags that are included on some
          pages; this is
          website-dependent.</p>

        <p class="lh-sm">
          <b>Anchor</b> is the text of the link that linked to the page. OSS won’t always find Anchors for all
          URLs,
          but here's an example:</p>

        <p class="lh-sm">The URL
          <a href="https://siarchives.si.edu/history/featured-topics/latin-american-research/international-exploring-expedition"
             target="_blank" class="link-primary">https://siarchives.si.edu/history/featured-topics/latin-american-research/international-exploring-expedition</a>
          has 2 anchors:</p>

        <p class="lh-sm">1. "Latin American Research", derived from the left-hand menu on this page:
          <a href="https://siarchives.si.edu/history/featured-topics" target="_blank" class="link-primary">https://siarchives.si.edu/history/featured-topics</a>
        </p>
        <p class="lh-sm">2. "view in English", derived from the English language link on this page:
          <a href="https://siarchives.si.edu/history/featured-topics/latin-american-research-es/international-exploring-expedition-0"
             target="_blank" class="link-primary">https://siarchives.si.edu/history/featured-topics/latin-american-research-es/international-exploring-expedition-0</a>
        </p>
      </div>
    </div>
  </div>
</template>

<script>
// @ is an alias to /src
import UserService from '@/services/user.service';

export default {
  name: 'Home',
  data() {
    return {
      content: '',
      serverStatus: ''
    };
  },
  mounted() {
    UserService.getPublicContent().then(
        (response) => {
          this.content = response.data;
        },
        (error) => {
          this.content =
              (error.response &&
                  error.response.data &&
                  error.response.data.message) ||
              error.message ||
              error.toString();
        }
    );
  }
};
</script>

<style scoped lang="scss">

</style>
