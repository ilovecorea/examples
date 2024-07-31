package helper

import (
	"context"
	"github.com/stretchr/testify/require"
	tc "github.com/testcontainers/testcontainers-go/modules/compose"
	"testing"
)

func StartComposeServices(t *testing.T, path string) {
	compose, err := tc.NewDockerCompose(path)
	require.NoError(t, err, "Failed to create compose")

	t.Cleanup(func() {
		require.NoError(t, compose.Down(context.Background(), tc.RemoveOrphans(true),
			tc.RemoveImagesLocal), "compose.Down()")
	})

	ctx, cancel := context.WithCancel(context.Background())
	t.Cleanup(cancel)

	require.NoError(t, compose.Up(ctx, tc.Wait(true)))
}
