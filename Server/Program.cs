using FirebaseAdmin;
using Google.Apis.Auth.OAuth2;
using Microsoft.AspNetCore.Mvc;
using Google.Cloud.Firestore;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

var app = builder.Build();

app.UseSwagger();
app.UseSwaggerUI();


try
{
    FirebaseApp.Create(new AppOptions
    {
        Credential = GoogleCredential.FromFile("firebasekey.json")
    });
    Console.WriteLine("Firebase initialized successfully!");
}
catch (Exception ex)
{
    Console.WriteLine($"ERROR: Firebase init failed - {ex.Message}");
    Console.WriteLine("Make sure firebasekey.json exists in the output directory.");
    return;
}

FirestoreDb db = new FirestoreDbBuilder
{
    ProjectId = "server-344dd",
    CredentialsPath = "firebasekey.json"
}.Build();


app.MapGet("/objects", async () =>
{
    var snapshot = await db.Collection("objects").GetSnapshotAsync();
    var objects = new List<Dictionary<string, object>>();
    foreach (var doc in snapshot.Documents)
    {
        var data = doc.ToDictionary();
        data["id"] = doc.Id;
        objects.Add(data);
    }
    return Results.Ok(objects);
});

app.MapPost("/objects", async ([FromBody] LostObject body) =>
{
    if (string.IsNullOrEmpty(body.Title))
    {
        return Results.BadRequest("Titlul este obligatoriu!");
    }

    var data = new Dictionary<string, object>
    {
        { "title", body.Title },
        { "description", body.Description ?? "" },
        { "category", body.Category ?? "pierdut" },
        { "address", body.Address ?? "" },
        { "latitude", body.Latitude },
        { "longitude", body.Longitude }
    };

    await db.Collection("objects").AddAsync(data);
    return Results.Ok("Obiect salvat cu succes!");
});

app.Run("http://0.0.0.0:5000");
public class LostObject
{
    public string Title { get; set; }
    public string Description { get; set; }
    public string Category { get; set; }
    public string Address { get; set; }
    public double Latitude { get; set; }
    public double Longitude { get; set; }
}
